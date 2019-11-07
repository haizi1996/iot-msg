package com.hailin.iot.remoting;

import com.hailin.iot.remoting.handler.MqttMessageHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
@Slf4j
public class RpcHandler extends ChannelInboundHandlerAdapter {

    private boolean serverSide ;

    private ConcurrentHashMap<MqttMessageType, UserProcessor<?>> userProcessors;

    public RpcHandler() {
        serverSide = false;
    }

    public RpcHandler(ConcurrentHashMap<MqttMessageType, UserProcessor<?>> userProcessors) {
        this(false , userProcessors);
    }

    public RpcHandler(boolean serverSide, ConcurrentHashMap<MqttMessageType, UserProcessor<?>> userProcessors) {
        this.serverSide = serverSide;
        this.userProcessors = userProcessors;
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug(msg.toString());
        if (msg instanceof MqttMessage){
            MqttMessageHandler.getHandler().handleMessage(new RemotingContext(ctx, new InvokeContext(), serverSide, userProcessors) ,(MqttMessage) msg);
        }
    }
}
