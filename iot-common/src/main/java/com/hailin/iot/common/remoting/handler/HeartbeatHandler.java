package com.hailin.iot.common.remoting.handler;

import com.hailin.iot.common.remoting.HeartbeatTrigger;
import com.hailin.iot.common.remoting.connection.Connection;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

@ChannelHandler.Sharable
public class HeartbeatHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            HeartbeatTrigger heartbeatTrigger = ctx.channel().attr(Connection.HEARTBEAT_TRIGGER).get();
            heartbeatTrigger.heartbeatTriggered(ctx);
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }
}
