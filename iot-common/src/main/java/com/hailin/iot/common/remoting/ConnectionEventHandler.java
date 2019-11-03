package com.hailin.iot.common.remoting;

import com.hailin.iot.common.remoting.config.switches.GlobalSwitch;
import com.hailin.iot.common.remoting.connection.Connection;
import com.hailin.iot.common.remoting.connection.Reconnector;
import com.hailin.iot.common.util.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
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
    @Setter
    protected ConnectionEventListener eventListener;

    private ConnectionEventExecutor eventExecutor;
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
}
