package com.hailin.iot.remoting.processor.impl;

import com.hailin.iot.remoting.DefaultBizContext;
import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;

public class MqttPubAckProcessor extends AbstractRemotingProcessor<MqttMessage> {

    @Override
    public void doProcess(RemotingContext ctx, MqttMessage msg) throws Exception {
        if(MqttMessageType.PUBACK.equals(msg.fixedHeader().messageType())){
            ctx.getUserProcessor(MqttMessageType.PUBACK).handleRequest(new DefaultBizContext(ctx) ,  msg);
        }
    }
}
