package com.hailin.iot.remoting;

import com.hailin.iot.remoting.handler.MessageHandler;
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

    private MessageHandler messageHandler;

    private ConnectionManager connectionManager;

    public RpcHandler() {
        serverSide = false;
    }

    public RpcHandler(ConnectionManager connectionManager ,ConcurrentHashMap<MqttMessageType, UserProcessor<?>> userProcessors , MessageHandler messageHandler) {
        this(false , connectionManager ,userProcessors , messageHandler);
    }

    public RpcHandler(boolean serverSide, ConnectionManager connectionManager , ConcurrentHashMap<MqttMessageType, UserProcessor<?>> userProcessors , MessageHandler messageHandler) {
        this.serverSide = serverSide;
        this.userProcessors = userProcessors;
        this.messageHandler = messageHandler;
        this.connectionManager = connectionManager;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("read a message ====> " + msg.toString());
        if (msg instanceof MqttMessage){
            messageHandler.handleMessage(new RemotingContext(ctx, new InvokeContext() , connectionManager, serverSide, userProcessors) ,(MqttMessage) msg);
        }
    }
}
