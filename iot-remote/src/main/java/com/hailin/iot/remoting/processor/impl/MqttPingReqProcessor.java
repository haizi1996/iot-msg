package com.hailin.iot.remoting.processor.impl;

import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import com.hailin.iot.remoting.util.RemotingUtil;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttPingReqProcessor extends AbstractRemotingProcessor<MqttMessage> {
    public static final Logger LOGGER = LoggerFactory.getLogger(MqttPingReqProcessor.class);

    @Override
    public void doProcess(RemotingContext ctx, MqttMessage msg) throws Exception {
        if (MqttMessageType.PINGREQ.equals(msg.fixedHeader().messageType())) {
            MqttMessage ack = MqttMessageFactory.newMessage(new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_LEAST_ONCE, false, 0), null, null);
            ctx.writeAndFlush(ack).addListener(future -> {
                if (future.isSuccess()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Send heartbeat ack done! heartbeat to remoteAddr={}",
                                RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
                    }
                } else {
                    LOGGER.error("Send heartbeat ack failed! heartbeat to remoteAddr={}",
                            RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
                }

            });
        } else {
            throw new RuntimeException("Cannot process command: " + msg.getClass().getName());
        }
    }
}
