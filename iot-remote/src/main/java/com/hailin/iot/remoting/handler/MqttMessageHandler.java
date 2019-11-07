package com.hailin.iot.remoting.handler;

import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.config.ConfigManager;
import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import com.hailin.iot.remoting.processor.impl.MqttConnAckProcessor;
import com.hailin.iot.remoting.processor.impl.MqttConnectProcessor;
import com.hailin.iot.remoting.processor.impl.MqttDisconnectProcessor;
import com.hailin.iot.remoting.processor.impl.MqttPingReqProcessor;
import com.hailin.iot.remoting.processor.impl.MqttPingRespProcessor;
import com.hailin.iot.remoting.processor.impl.MqttPubAckProcessor;
import com.hailin.iot.remoting.processor.impl.MqttPublishProcessor;
import com.hailin.iot.remoting.processor.ProcessorManager;
import com.hailin.iot.remoting.processor.RemotingProcessor;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * mqtt协议的处理器
 * @author zhanghailin
 */

public class MqttMessageHandler implements MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttMessageHandler.class);

    private ProcessorManager processorManager;

    private static MqttMessageHandler handler = new  MqttMessageHandler();

    public static MqttMessageHandler getHandler() {
        return handler;
    }

    private MqttMessageHandler() {
        this.processorManager = new ProcessorManager();
        //MqttConnect request
        this.processorManager.registerProcessor(MqttMessageType.CONNECT, new MqttConnectProcessor());
//        process response
        this.processorManager.registerProcessor(MqttMessageType.CONNACK, new MqttConnAckProcessor());
//
        this.processorManager.registerProcessor(MqttMessageType.PUBLISH, new MqttPublishProcessor());
        this.processorManager.registerProcessor(MqttMessageType.PINGREQ, new MqttPingReqProcessor());
        this.processorManager.registerProcessor(MqttMessageType.PINGRESP, new MqttPingRespProcessor());
        this.processorManager.registerProcessor(MqttMessageType.PUBACK, new MqttPubAckProcessor());
        this.processorManager.registerProcessor(MqttMessageType.DISCONNECT, new MqttDisconnectProcessor());

        this.processorManager
                .registerDefaultProcessor(new AbstractRemotingProcessor<MqttMessage>() {
                    @Override
                    public void doProcess(RemotingContext ctx, MqttMessage msg) throws Exception {
                        LOGGER.error("No processor available for message type {}, msg {}",
                                msg.fixedHeader().messageType(), msg);
                    }
                });
    }

    @Override
    public void handleMessage(RemotingContext ctx, MqttMessage msg) throws Exception {
        try {
            if (msg instanceof List) {
                final Runnable handleTask = new Runnable() {
                    @Override
                    public void run() {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Batch message! size={}", ((List<?>) msg).size());
                        }
                        for (final Object m : (List<?>) msg) {
                            MqttMessageHandler.this.process(ctx, m);
                        }
                    }
                };
                if (ConfigManager.dispatch_msg_list_in_default_executor()) {
                    // If msg is list ,then the batch submission to biz threadpool can save io thread.
                    // See com.alipay.remoting.decoder.ProtocolDecoder
                    processorManager.getDefaultExecutor().execute(handleTask);
                } else {
                    handleTask.run();
                }
            } else {
                process(ctx, msg);
            }
        } catch (final Throwable t) {
            processException(ctx, msg, t);
        }
    }

    private void process(RemotingContext ctx, Object msg) {
        try {
            final MqttMessage message = (MqttMessage)msg;
            final RemotingProcessor processor = processorManager.getProcessor(message.fixedHeader().messageType());
            processor.process(ctx, message, processorManager.getDefaultExecutor());
        }catch (final Throwable throwable){
            processException(ctx, msg, throwable);
        }
    }

    private void processException(RemotingContext ctx, Object msg, Throwable throwable) {
        if (msg instanceof List) {
            for (final Object m : (List<?>) msg) {
                processExceptionForSingleMessage(ctx, m, throwable);
            }
        } else {
            processExceptionForSingleMessage(ctx, msg, throwable);
        }
    }

    private void processExceptionForSingleMessage(RemotingContext ctx, Object m, Throwable throwable) {
        if(m instanceof MqttMessage){
            MqttMessage message = (MqttMessage) m;

            if(!message.fixedHeader().qosLevel().equals(MqttQoS.AT_MOST_ONCE)){
                Object obj = message.variableHeader();

                if (throwable instanceof RejectedExecutionException) {
                    MqttMessage response = new MqttMessage(null , null , null);
//                    final ResponseCommand response = this.commandFactory.createExceptionResponse(
//                            id, ResponseStatus.SERVER_THREADPOOL_BUSY);
                    // RejectedExecutionException here assures no response has been sent back
                    // Other exceptions should be processed where exception was caught, because here we don't known whether ack had been sent back.


                    ctx.getChannelContext().writeAndFlush(response)
                            .addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    if (future.isSuccess()) {
                                        if (LOGGER.isInfoEnabled()) {
                                            LOGGER.info("Write back exception response done,status={}",
                                                            response);
                                        }
                                    } else {
                                        LOGGER.error("Write back exception response failed, ",
                                                future.cause());
                                    }
                                }

                            });
                }
            }
        }
    }

    @Override
    public void registerProcessor(MqttMessageType mqttMessageType, RemotingProcessor<?> processor) {
        this.processorManager.registerProcessor(mqttMessageType, processor);
    }

    @Override
    public void registerDefaultExecutor(ExecutorService executor) {
        this.processorManager.registerDefaultExecutor(executor);
    }

    @Override
    public ExecutorService getDefaultExecutor() {
        return this.processorManager.getDefaultExecutor();
    }
}
