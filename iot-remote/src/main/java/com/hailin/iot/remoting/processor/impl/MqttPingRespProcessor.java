package com.hailin.iot.remoting.processor.impl;

import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.future.InvokeFuture;
import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import com.hailin.iot.remoting.util.RemotingUtil;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttPingRespProcessor extends AbstractRemotingProcessor <MqttMessage>{

    public static final Logger LOGGER = LoggerFactory.getLogger(MqttPingRespProcessor.class);

    @Override
    public void preProcessRemotingContext(RemotingContext ctx, MqttMessage msg , long timestamp) throws Exception {
       if (MqttMessageType.PINGRESP.equals(msg.fixedHeader().messageType())) {
            Connection conn = ctx.getChannelContext().channel().attr(Connection.CONNECTION).get();
            InvokeFuture future = conn.getHeartbeatFuture();
            if (future != null) {
                future.cancelTimeout();
                try {
                    future.executeInvokeCallback();
                } catch (Exception e) {
                    LOGGER.error("Exception caught when executing heartbeat invoke callback. From {}",
                            RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()), e);
                }
            } else {
                LOGGER.warn("Cannot find heartbeat InvokeFuture, maybe already timeout. heartbeat From {}",
                        RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
            }
        } else {
            throw new RuntimeException("Cannot process command: " + msg.getClass().getName());
        }
    }
}
