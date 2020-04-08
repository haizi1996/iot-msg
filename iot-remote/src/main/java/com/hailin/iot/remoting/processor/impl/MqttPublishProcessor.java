package com.hailin.iot.remoting.processor.impl;

import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.RpcAsyncContext;
import com.hailin.iot.remoting.UserProcessor;
import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import com.hailin.iot.remoting.processor.AsyncUserProcessor;
import com.hailin.iot.remoting.util.RemotingUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import static com.hailin.iot.remoting.InvokeContext.IOT_PROCESS_WAIT_TIME;

public class MqttPublishProcessor extends AbstractRemotingProcessor<MqttPublishMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttPublishProcessor.class);

    private static final int TIMEOUT = 500;

    @Override
    public void preProcessRemotingContext(RemotingContext ctx, MqttPublishMessage msg , long timestamp) throws Exception {



    }



    private void preProcessRemotingContext(RemotingContext ctx) {
        long currentTimestamp = System.currentTimeMillis();
        ctx.getInvokeContext().putIfAbsent(IOT_PROCESS_WAIT_TIME , currentTimestamp - ctx.getArriveTimestamp());
        ctx.setTimeout(TIMEOUT);
    }


    /**
     * 根据请求消息发送响应的消息
     * @param ctx
     * @param message  请求的消息
     */
    public void sendResponseIfNecessary(RemotingContext ctx, MqttMessage message) {
        if(!(message instanceof  MqttPublishMessage)){
            return;
        }
        MqttFixedHeader fixedHeader = message.fixedHeader();
        MqttPublishVariableHeader header = ((MqttPublishMessage) message).variableHeader();
        MqttMessage response = null;
        final int id = header.packetId();
        if (MqttQoS.EXACTLY_ONCE == fixedHeader.qosLevel()) {
            response = new MqttMessage(new MqttFixedHeader(MqttMessageType.PUBREC , false ,MqttQoS.EXACTLY_ONCE , false , 0),
                    MqttMessageIdVariableHeader.from(id));
        } else if(MqttQoS.AT_LEAST_ONCE == fixedHeader.qosLevel()){
            response = new MqttMessage(new MqttFixedHeader(MqttMessageType.PUBACK , false ,MqttQoS.EXACTLY_ONCE , false , 0),
                    MqttMessageIdVariableHeader.from(id));
        }else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Oneway rpc request received, do not send response, id=" + id+ ", the address is "+ RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
            }
        }
        if (Objects.nonNull(response)){
            ctx.writeAndFlush(response).addListener((ChannelFutureListener) future -> {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Rpc response sent! requestId="+ id + ". The address is " + RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
                }
                if (!future.isSuccess()) {
                    LOGGER.error("Rpc response send failed! id="+ id + ". The address is "+ RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()), future.cause());
                }
            });
        }
    }
}
