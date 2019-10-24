package com.hailin.iot.common.remoting.processor;

import com.hailin.iot.common.remoting.RemotingContext;
import com.hailin.iot.common.remoting.connection.Connection;
import com.hailin.iot.common.remoting.future.InvokeFuture;
import com.hailin.iot.common.util.RemotingUtil;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttHeartBeatProcessor extends AbstractRemotingProcessor {

    public static final Logger LOGGER = LoggerFactory.getLogger(MqttHeartBeatProcessor.class);

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
        } else if (MqttMessageType.PINGRESP.equals(msg.fixedHeader().messageType())) {
            Connection conn = ctx.getChannelContext().channel().attr(Connection.CONNECTION).get();
            InvokeFuture future = conn.getHeartbeatFuture();
            if (future != null) {
                future.putResponse(null);
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
