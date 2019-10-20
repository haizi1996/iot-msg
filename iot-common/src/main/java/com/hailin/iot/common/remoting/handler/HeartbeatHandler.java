package com.hailin.iot.common.remoting.handler;

import com.hailin.iot.common.remoting.connection.Connection;
import com.hailin.iot.common.remoting.protocol.Protocol;
import com.hailin.iot.common.remoting.protocol.ProtocolCode;
import com.hailin.iot.common.remoting.protocol.ProtocolManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

@ChannelHandler.Sharable
public class HeartbeatHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            ProtocolCode protocolCode = ctx.channel().attr(Connection.PROTOCOL).get();
            Protocol protocol = ProtocolManager.getProtocol(protocolCode);
            protocol.getHeartbeatTrigger().heartbeatTriggered(ctx);
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }
}
