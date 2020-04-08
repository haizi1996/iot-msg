package com.hailin.iot.remoting.factory.impl;

import com.hailin.iot.remoting.ConnectionEventHandler;
import com.hailin.iot.remoting.ConnectionEventType;
import com.hailin.iot.remoting.ConnectionManager;
import com.hailin.iot.remoting.NamedThreadFactory;
import com.hailin.iot.remoting.Url;
import com.hailin.iot.remoting.codec.Codec;
import com.hailin.iot.remoting.config.ConfigManager;
import com.hailin.iot.remoting.config.configs.ConfigurableInstance;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.factory.ConnectionFactory;
import com.hailin.iot.remoting.util.NettyEventLoopUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * 抽象的连接工厂
 * @author hailin
 */
public abstract class AbstractConnectionFactory implements ConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConnectionFactory.class);

    private static final EventLoopGroup workerGroup = NettyEventLoopUtil.newEventLoopGroup(Runtime.getRuntime().availableProcessors()+1 , new NamedThreadFactory("iot-netty-client-worker" , true));

    private final Codec codec;

    private final ConfigurableInstance confInstance;

    private final ChannelHandler heartbeatHandler;

    private final ChannelHandler handler;

    protected Bootstrap bootstrap ;

    public AbstractConnectionFactory(Codec codec, ChannelHandler heartbeatHandler, ChannelHandler handler , ConfigurableInstance confInstance) {
        this.codec = codec;
        this.heartbeatHandler = heartbeatHandler;
        this.handler = handler;
        this.confInstance = confInstance;
    }

    @Override
    public void init(ConnectionEventHandler connectionEventHandler) {
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NettyEventLoopUtil.getClientSocketChannelClass())
                .option(ChannelOption.TCP_NODELAY, ConfigManager.tcp_nodelay())
                .option(ChannelOption.SO_REUSEADDR, ConfigManager.tcp_so_reuseaddr())
                .option(ChannelOption.SO_KEEPALIVE, ConfigManager.tcp_so_keepalive());

        // init netty write buffer water mark
        initWriteBufferWaterMark();

        // init byte buf allocator
        if (ConfigManager.netty_buffer_pooled()) {
            this.bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        } else {
            this.bootstrap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        }

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("decoder", codec.newDecoder());
                pipeline.addLast("encoder", codec.newEncoder());

                boolean idleSwitch = ConfigManager.tcp_idle_switch();
                if (idleSwitch) {
                    pipeline.addLast("idleStateHandler",
                            new IdleStateHandler(ConfigManager.tcp_idle(), ConfigManager.tcp_idle(), 0,
                                    TimeUnit.MILLISECONDS));
                    pipeline.addLast("heartbeatHandler", heartbeatHandler);
                }

                pipeline.addLast("connectionEventHandler", connectionEventHandler);
                pipeline.addLast("handler", handler);
            }
        });
    }
    /**
     * init netty write buffer water mark
     */
    private void initWriteBufferWaterMark() {
        int lowWaterMark = this.confInstance.netty_buffer_low_watermark();
        int highWaterMark = this.confInstance.netty_buffer_high_watermark();
        if (lowWaterMark > highWaterMark) {
            throw new IllegalArgumentException(
                    String.format("[client side] iot netty high water mark {%s} should not be smaller than low water mark {%s} bytes)",
                                    highWaterMark, lowWaterMark));
        } else {
            LOGGER.warn(
                    "[client side] iot netty low water mark is {} bytes, high water mark is {} bytes",
                    lowWaterMark, highWaterMark);
        }
        this.bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(
                lowWaterMark, highWaterMark));
    }
    @Override
    public Connection createConnection(Url url  , ConnectionManager connectionManager) throws Exception {
        Channel channel = doCreateConnection(url.getIp(), url.getPort(), url.getConnectTimeout());
        Connection conn = new Connection(channel , url);
        channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        return conn;
    }

    @Override
    public Connection createConnection(String targetIp, int targetPort, int connectTimeOut , ConnectionManager connectionManager) throws Exception {
        Channel channel = doCreateConnection(targetIp, targetPort, connectTimeOut);
        Connection conn = new Connection(channel,   new Url(targetIp, targetPort) );
        channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        return conn;
    }

//    @Override
//    public Connection createConnection(String targetIP, int targetPort, byte version, int connectTimeout) throws Exception {
//        Channel channel = doCreateConnection(targetIP, targetPort, connectTimeout);
//        Connection conn = new Connection(channel,
//                ProtocolCode.fromBytes(MQTTProtocol.PROTOCOL_CODE), version, new Url(targetIP,
//                targetPort));
//        channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECTION);
//        return conn;
//    }

    protected Channel doCreateConnection(String targetIP, int targetPort, int connectTimeout)
            throws Exception {
        // prevent unreasonable value, at least 1000
        connectTimeout = Math.max(connectTimeout, 1000);
        String address = targetIP + ":" + targetPort;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connectTimeout of address [{}] is [{}].", address, connectTimeout);
        }
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(targetIP, targetPort));

        future.awaitUninterruptibly();
        if (!future.isDone()) {
            String errMsg = "Create connection to " + address + " timeout!";
            LOGGER.warn(errMsg);
            throw new Exception(errMsg);
        }
        if (future.isCancelled()) {
            String errMsg = "Create connection to " + address + " cancelled by user!";
            LOGGER.warn(errMsg);
            throw new Exception(errMsg);
        }
        if (!future.isSuccess()) {
            String errMsg = "Create connection to " + address + " error!";
            LOGGER.warn(errMsg);
            throw new Exception(errMsg, future.cause());
        }
        return future.channel();
    }
}
