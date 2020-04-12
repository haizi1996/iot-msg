package com.hailin.iot.remoting;

import com.hailin.iot.remoting.config.switches.GlobalSwitch;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.connection.Reconnector;
import com.hailin.iot.remoting.util.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 记录时间状态的日志
 * @author hailin
 */
@ChannelHandler.Sharable

public class ConnectionEventHandler extends ChannelDuplexHandler {

    
    private AtomicInteger count = new AtomicInteger(0);



    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConnectionEventHandler.class);

    private static final Logger COUNT_LOGGER = LoggerFactory.getLogger(ConnectionEventHandler.class);

    @Getter
    @Setter
    private ConnectionManager connectionManager;
    @Getter
    protected ConnectionEventListener eventListener;

    protected ConnectionEventExecutor eventExecutor;
    @Getter
    @Setter
    private Reconnector reconnectManager;

    private GlobalSwitch globalSwitch;

    private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1,
            new NamedThreadFactory("Iot-msg-conn-event-executor", true));

    public ConnectionEventHandler() {

    }

    public ConnectionEventHandler(GlobalSwitch globalSwitch) {
        this.globalSwitch = globalSwitch;
        executor.scheduleAtFixedRate(() -> COUNT_LOGGER.info("当前连接数 : {}" , count  ) , 5 , 5 , TimeUnit.SECONDS);

    }


    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        final String local = localAddress == null ? null : RemotingUtil.parseSocketAddressToString(localAddress);
        final String remote = remoteAddress == null ? "UNKNOWN" : RemotingUtil.parseSocketAddressToString(remoteAddress);
        if (Objects.isNull(local)){
            LOGGER.info("Try connect to {}", remote);
        }else {
            LOGGER.info("Try connect from {} to {}", local, remote);
        }
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        LOGGER.info("Connection disconnect to {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        super.disconnect(ctx, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        LOGGER.info("Connection closed: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        final Connection conn = ctx.channel().attr(Connection.CONNECTION).get();
        if (conn != null) {
//            conn.onClose();
        }

        super.close(ctx, promise);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
        if (event instanceof ConnectionEventType) {
            switch ((ConnectionEventType) event) {
                case CONNECT:
                    Channel channel = ctx.channel();
                    if (null != channel) {
                        Attribute<Connection> connection = channel.attr(Connection.CONNECTION);
                        if (Objects.nonNull(connection)) {
                            this.onEvent(connection.get(), connection.get().getUrl().getOriginUrl(),
                                    ConnectionEventType.CONNECT);
                        }
                    } else {
                        LOGGER.warn("channel null when handle user triggered event in ConnectionEventHandler!");
                    }
                    break;
                default:
                    break;
            }
        } else {
            super.userEventTriggered(ctx, event);
        }
    }
    private void onEvent(final Connection conn, final String remoteAddress,
                         final ConnectionEventType type) {
        if (this.eventListener != null) {
            this.eventExecutor.onEvent(new Runnable() {
                @Override
                public void run() {
                    ConnectionEventHandler.this.eventListener.onEvent(type, remoteAddress, conn);
                }
            });
        }
    }
    public void setConnectionEventListener(ConnectionEventListener listener) {
        if (listener != null) {
            this.eventListener = listener;
            if (this.eventExecutor == null) {
                this.eventExecutor = new ConnectionEventExecutor();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String remoteAddress = RemotingUtil.parseRemoteAddress(ctx.channel());
        final String localAddress = RemotingUtil.parseLocalAddress(ctx.channel());
        LOGGER.warn("ExceptionCaught in connection: local[{}], remote[{}], close the connection! Cause[{}:{}]",
                        localAddress, remoteAddress, cause.getClass().getSimpleName(), cause.getMessage());
        ctx.channel().close();
    }

    public class ConnectionEventExecutor {
        private Logger logger = LoggerFactory.getLogger(ConnectionEventExecutor.class);
        ExecutorService executor = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10000),
                new NamedThreadFactory("Iot-msg-conn-event-executor", true));

        /**
         * Process event.
         *
         * @param runnable Runnable
         */
        public void onEvent(Runnable runnable) {
            try {
                executor.execute(runnable);
            } catch (Throwable t) {
                logger.error("Exception caught when execute connection event!", t);
            }
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("Connection channel registered: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("Connection channel unregistered: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("Connection channel active: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        count.incrementAndGet();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String remoteAddress = RemotingUtil.parseRemoteAddress(ctx.channel());
        LOGGER.debug("Connection channel inactive: {}", remoteAddress);
        count.decrementAndGet();
        super.channelInactive(ctx);
        Attribute attr = ctx.channel().attr(Connection.CONNECTION);
        if (null != attr) {
            // add reconnect task
            if (this.globalSwitch != null
                    && this.globalSwitch.isOn(GlobalSwitch.CONN_RECONNECT_SWITCH)) {
                Connection conn = (Connection) attr.get();
                if (reconnectManager != null) {
                    reconnectManager.reconnect(conn.getUrl());
                }
            }
            // trigger close connection event
            onEvent((Connection) attr.get(), remoteAddress, ConnectionEventType.CLOSE);
        }
    }



    /**
     * Getter method for property <tt>listener</tt>.
     *
     * @return property value of listener
     */
    public ConnectionEventListener getConnectionEventListener() {
        return eventListener;
    }


    /**
     * Getter method for property <tt>connectionManager</tt>.
     *
     * @return property value of connectionManager
     */
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }


    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }



    public void setReconnector(Reconnector reconnector) {
        this.reconnectManager = reconnector;
    }



}
