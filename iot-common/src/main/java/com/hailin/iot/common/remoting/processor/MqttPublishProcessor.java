package com.hailin.iot.common.remoting.processor;

import com.hailin.iot.common.remoting.RemotingContext;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

public class MqttPublishProcessor extends AbstractRemotingProcessor<MqttPublishMessage> {
    @Override
    public void doProcess(RemotingContext ctx, MqttPublishMessage msg) throws Exception {

    }
}
