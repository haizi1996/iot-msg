package com.hailin.iot.broker.remoting;

import com.hailin.iot.common.remoting.*;
import com.hailin.iot.common.remoting.codec.Codec;
import com.hailin.iot.common.remoting.config.ConfigManager;
import com.hailin.iot.common.remoting.config.switches.GlobalSwitch;
import com.hailin.iot.common.remoting.connection.Connection;
import com.hailin.iot.common.util.NettyEventLoopUtil;
import com.hailin.iot.common.util.RemotingUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class RpcServer extends AbstractRemotingServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private ServerBootstrap bootstrap ;

    @Getter
    private ChannelFuture channelFuture ;

    private ConnectionEventHandler connectionEventHandler;

    private ConnectionEventListener connectionEventListener = new ConnectionEventListener();

    private ConcurrentHashMap<String , UserProcessor<?>> userProcessors = new ConcurrentHashMap<>(4);

    private final EventLoopGroup bossGroup = NettyEventLoopUtil.newEventLoopGroup(1, new NamedThreadFactory(
                            "Rpc-netty-server-boss", false));

    private static final EventLoopGroup workerGroup = NettyEventLoopUtil.newEventLoopGroup(Runtime
                            .getRuntime()
                            .availableProcessors() * 2,
                    new NamedThreadFactory("Rpc-netty-server-worker", true));

    private RemotingAddressParser addressParser;

    private DefaultServerConnectionManager connectionManager;

    protected RpcRemoting rpcRemoting;

    private Codec codec;

    static {
        if (workerGroup instanceof NioEventLoopGroup){
            ((NioEventLoopGroup)workerGroup).setIoRatio(ConfigManager.netty_io_ratio());
        }else if (workerGroup instanceof EpollEventLoopGroup){
            ((EpollEventLoopGroup)workerGroup).setIoRatio(ConfigManager.netty_io_ratio());
        }
    }

    public RpcServer(int port) {
        this(port, false);
    }

    public RpcServer(String ip, int port) {
        this(ip, port, false);
    }

    public RpcServer(int port , boolean manageConnection){
        super(port);
        if (manageConnection){
            this.switches().turnOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH);
        }
    }
    public RpcServer(String ip, int port, boolean manageConnection) {
        super(ip, port);
        if (manageConnection) {
            this.switches().turnOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH);
        }
    }
    public RpcServer(int port, boolean manageConnection, boolean syncStop) {
        this(port, manageConnection);
        if (syncStop) {
            this.switches().turnOn(GlobalSwitch.SERVER_SYNC_STOP);
        }
    }

    @Override
    protected void doInit() {
        if (this.addressParser == null){
            this.addressParser = new RpcAddressParser();
        }
        if (this.switches().isOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH)){
            ConnectionSelectStrategy selectStrategy = new RandomSelectStrategy(null);
            this.connectionManager = new DefaultServerConnectionManager(selectStrategy);
            this.connectionManager.startup();

            this.connectionEventHandler = new RpcConnectionEventHandler(switches());
            this.connectionEventHandler.setConnectionManager(this.connectionManager);
            this.connectionEventHandler.setConnectionEventListener(this.connectionEventListener);
        }else {
            this.connectionEventHandler = new ConnectionEventHandler(switches());
            this.connectionEventHandler.setConnectionEventListener(this.connectionEventListener);
        }
        initRpcRemoting();

        this.bootstrap = new ServerBootstrap();
        this.bootstrap.group(bossGroup , workerGroup)
                .channel(NettyEventLoopUtil.getServerSocketChannelClass())
                .option(ChannelOption.SO_BACKLOG , ConfigManager.tcp_so_backlog())
                .option(ChannelOption.SO_REUSEADDR , ConfigManager.tcp_so_reuseaddr())
                .childOption(ChannelOption.TCP_NODELAY , ConfigManager.tcp_nodelay())
                .childOption(ChannelOption.SO_KEEPALIVE , ConfigManager.tcp_so_keepalive());
        //初始化写缓冲
        initWriteBufferWaterMark();

        //初始化字节缓存的分配器
        if (ConfigManager.netty_buffer_pooled()){
            this.bootstrap.option(ChannelOption.ALLOCATOR , PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR , PooledByteBufAllocator.DEFAULT);
        }else {
            this.bootstrap.option(ChannelOption.ALLOCATOR , UnpooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR , UnpooledByteBufAllocator.DEFAULT);
        }
        //允许水平触发模式的epoll
        NettyEventLoopUtil.enableTriggeredMode(bootstrap);

        final boolean idleSwitch = ConfigManager.tcp_idle_switch();
        final int idleTime = ConfigManager.tcp_server_idle();
        final ChannelHandler serverIdleHandler = new ServerIdleHandler();
        final RpcHandler rpcHandler = new RpcHandler();
        this.bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast("decoder" , codec.newDecoder());
                pipeline.addLast("encoder" , codec.newEncoder());
                if (idleSwitch){
                    pipeline.addLast("idleStateHandler" , new IdleStateHandler(0 , 0 , idleTime , TimeUnit.SECONDS));
                    pipeline.addLast("serverIdleHandler" , serverIdleHandler);
                }
                pipeline.addLast("connectionEventHandler" , connectionEventHandler);
                pipeline.addLast("handler" , rpcHandler);
                createConncetion(socketChannel);
            }

            /**
             * 创建connection对象
             * @param socketChannel socket
             */
            private void createConncetion(SocketChannel socketChannel) {
                Url url = addressParser.parse(RemotingUtil.parseRemoteAddress(socketChannel));
                if (switches().isOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH)){
                    connectionManager.add(new Connection(socketChannel , url) , url.getUniqueKey());
                }else {
                    new Connection(socketChannel , url);
                }
                socketChannel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECTION);
            }
        });

    }

    private void initWriteBufferWaterMark() {
        int lowWaterMark = this.netty_buffer_low_watermark();
        int highWaterMark = this.netty_buffer_high_watermark();
        if (lowWaterMark > highWaterMark){
            throw new IllegalArgumentException(
                    String
                            .format(
                                    "[server side] iot netty high water mark {%s} should not be smaller than low water mark {%s} bytes)",
                                    highWaterMark, lowWaterMark));
        } else {
            LOGGER.warn(
                    "[server side] iot netty low water mark is {} bytes, high water mark is {} bytes",
                    lowWaterMark, highWaterMark);
        }
        this.bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(
                lowWaterMark, highWaterMark));
    }

    public void initRpcRemoting() {
        this.rpcRemoting = new RpcServerRemoting(null , this.addressParser , this.connectionManager);
    }

    @Override
    protected boolean doStart() throws InterruptedException{
        this.channelFuture = this.bootstrap.bind(new InetSocketAddress(ip() , port())).sync();
        return this.channelFuture.isSuccess();
    }

    @Override
    protected boolean doStop() {
        if (Objects.nonNull(this.channelFuture)){
            this.channelFuture.channel().close();
        }
        if (this.switches().isOn(GlobalSwitch.SERVER_SYNC_STOP)){
            this.bossGroup.shutdownGracefully().awaitUninterruptibly();
        }else {
            this.bossGroup.shutdownGracefully();
        }
        if (this.switches().isOn(GlobalSwitch.SERVER_MANAGE_CONNECTION_SWITCH)
        && Objects.nonNull(this.connectionManager)){
            this.connectionManager.shutdown();
            LOGGER.warn("Close all connections from server side!");
        }
        LOGGER.warn("Rpc Server stopped!");
        return true;
    }

    public void addConnectionEventProcessor(ConnectionEventType type , ConnectionEventProcessor processor) {
        this.connectionEventListener.addConnectionEventProcessor(type , processor);
    }


}
