package com.hailin.iot.remoting.processor.impl;

import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MqttDisconnectProcessor extends AbstractRemotingProcessor<MqttMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttDisconnectProcessor.class);

    @Override
    public void preProcessRemotingContext(RemotingContext ctx, MqttMessage msg , long timestamp) throws Exception {

        MqttFixedHeader header = msg.fixedHeader();
        if(!Objects.equals(header.messageType() , MqttMessageType.DISCONNECT )){
            return;
        }
        LOGGER.debug("the connection will is disconnected!");
        ctx.getChannelContext().disconnect();

    }
}
