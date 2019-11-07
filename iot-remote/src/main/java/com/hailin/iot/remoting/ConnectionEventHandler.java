package com.hailin.iot.remoting;

import com.hailin.iot.remoting.config.switches.GlobalSwitch;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.connection.Reconnector;
import com.hailin.iot.common.util.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.Attribute;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 记录时间状态的日志
 * @author hailin
 */
@ChannelHandler.Sharable
@Slf4j
public class ConnectionEventHandler extends ChannelDuplexHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionEventHandler.class);

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

    public ConnectionEventHandler() {

    }

    public ConnectionEventHandler(GlobalSwitch globalSwitch) {
        this.globalSwitch = globalSwitch;
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
            conn.onClose();
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
                        Connection connection = channel.attr(Connection.CONNECTION).get();
                        this.onEvent(connection, connection.getUrl().getOriginUrl(),
                                ConnectionEventType.CONNECT);
                    } else {
                        log.warn("channel null when handle user triggered event in ConnectionEventHandler!");
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
    public class ConnectionEventExecutor {
        private  Logger logger = LoggerFactory.getLogger(ConnectionEventExecutor.class);
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
        log.debug("Connection channel registered: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("Connection channel unregistered: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Connection channel active: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String remoteAddress = RemotingUtil.parseRemoteAddress(ctx.channel());
        log.debug("Connection channel inactive: {}", remoteAddress);
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
