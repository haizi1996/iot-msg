package com.hailin.iot.remoting.handler;

import com.hailin.iot.remoting.HeartbeatTrigger;
import com.hailin.iot.remoting.MqttHeartbeatTrigger;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

@ChannelHandler.Sharable
public class HeartbeatHandler extends ChannelDuplexHandler {

    private HeartbeatTrigger heartbeatTrigger = new MqttHeartbeatTrigger();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent){
            heartbeatTrigger.heartbeatTriggered(ctx);
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
