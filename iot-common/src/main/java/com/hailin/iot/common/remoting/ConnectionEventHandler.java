package com.hailin.iot.common.remoting;

import com.hailin.iot.common.remoting.config.switches.GlobalSwitch;
import com.hailin.iot.common.remoting.connection.Connection;
import com.hailin.iot.common.remoting.connection.Reconnector;
import com.hailin.iot.common.util.RemotingUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Objects;

/**
 * 记录时间状态的日志
 * @author hailin
 */
@ChannelHandler.Sharable
public class ConnectionEventHandler extends ChannelDuplexHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionEventHandler.class);

    @Getter
    @Setter
    private ConnectionManager connectionManager;
    @Getter
    @Setter
    private ConnectionEventListener connectionEventListener;

//    private ConnectionEventExecutor eventExecutor;
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
}
