package com.hailin.iot.remoting.processor.impl;

import com.hailin.iot.common.model.Message;
import com.hailin.iot.remoting.InvokeContext;
import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.RpcAsyncContext;
import com.hailin.iot.remoting.UserProcessor;
import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import com.hailin.iot.remoting.processor.AsyncUserProcessor;
import com.hailin.iot.common.util.MessageUtil;
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
    public void doProcess(RemotingContext ctx, MqttPublishMessage msg) throws Exception {

        preProcessRemotingContext(ctx);
        if (ctx.isTimeoutDiscard() && ctx.isRequestTimeout()) {
            return;// then, discard this request
        }
        // decode request all
        dispatchToUserProcessor(ctx, msg);

    }

    @Override
    public void process(RemotingContext ctx, MqttPublishMessage msg, ExecutorService defaultExecutor) throws Exception {
        MqttFixedHeader fixedHeader = msg.fixedHeader();
        UserProcessor userProcessor = ctx.getUserProcessor(fixedHeader.messageType());
        if (userProcessor == null) {
            String errMsg = "No user processor found for request: " + fixedHeader.messageType();
            LOGGER.error(errMsg);
            return;// must end process
        }

        // set timeout check state from user's processor
        ctx.setTimeoutDiscard(userProcessor.timeoutDiscard());

        // to check whether to process in io thread
        if (userProcessor.processInIOThread()) {

            // process in io thread
            new ProcessTask(ctx, msg).run();
            return;// end
        }

        Executor executor;
        // to check whether get executor using executor selector
        if (null == userProcessor.getExecutorSelector()) {
            executor = userProcessor.getExecutor();
        } else {

            //try get executor with strategy
            executor = userProcessor.getExecutorSelector().select(fixedHeader.messageType(),
                    msg);
        }

        // Till now, if executor still null, then try default
        if (executor == null) {
            executor = (this.getExecutor() == null ? defaultExecutor : this.getExecutor());
        }

        // use the final executor dispatch process task
        executor.execute(new ProcessTask(ctx, msg));
    }

    private void preProcessRemotingContext(RemotingContext ctx) {
        long currentTimestamp = System.currentTimeMillis();
        ctx.getInvokeContext().putIfAbsent(IOT_PROCESS_WAIT_TIME , currentTimestamp - ctx.getArriveTimestamp());
        ctx.setTimeout(TIMEOUT);
    }

    /**
     * dispatch request command to user processor
     * @param ctx remoting context
     * @param msg rpc request command
     */
    private void dispatchToUserProcessor(RemotingContext ctx, MqttPublishMessage msg) {
        MqttPublishVariableHeader variableHeader = msg.variableHeader();
        MqttFixedHeader fixedHeader = msg.fixedHeader();
        final int packetId = variableHeader.packetId();
        final MqttMessageType messageType = fixedHeader.messageType();
        // processor here must not be null, for it have been checked before
        UserProcessor processor = ctx.getUserProcessor(messageType);
        if (processor instanceof AsyncUserProcessor) {
            try {
                processor.handleRequest(processor.preHandleRequest(ctx, msg), new RpcAsyncContext(ctx, msg, this), msg);
                sendResponseIfNecessary(ctx,  msg);
            } catch (RejectedExecutionException e) {
                LOGGER.warn("RejectedExecutionException occurred when do ASYNC process in RpcRequestProcessor");
            } catch (Throwable t) {
                String errMsg = "AYSNC process rpc request failed in RpcRequestProcessor, id=" + packetId;
                LOGGER.error(errMsg, t);
            }
        } else {
            try {
               processor.handleRequest(processor.preHandleRequest(ctx, msg), msg);
                sendResponseIfNecessary(ctx,  msg);
            } catch (RejectedExecutionException e) {
                LOGGER.warn("RejectedExecutionException occurred when do SYNC process in RpcRequestProcessor");
            } catch (Throwable t) {
                String errMsg = "SYNC process rpc request failed in RpcRequestProcessor, id=" + packetId;
                LOGGER.error(errMsg, t);
            }
        }
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
