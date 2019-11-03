package com.hailin.iot.common.remoting.processor;

import com.hailin.iot.common.remoting.DefaultBizContext;
import com.hailin.iot.common.remoting.RemotingContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;

public class MqttPubAckMessageProcessor extends AbstractRemotingProcessor<MqttMessage> {

    @Override
    public void doProcess(RemotingContext ctx, MqttMessage msg) throws Exception {
        if(MqttMessageType.PUBACK.equals(msg.fixedHeader().messageType())){
            ctx.getUserProcessor(MqttMessageType.PUBACK).handleRequest(new DefaultBizContext(ctx) ,  msg);
        }
    }
}
