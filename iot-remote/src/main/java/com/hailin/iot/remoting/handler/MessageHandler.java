package com.hailin.iot.remoting.handler;

import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.processor.RemotingProcessor;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.concurrent.ExecutorService;

/**
 * 消息处理的接口
 */
public interface MessageHandler {

    void handleMessage(RemotingContext ctx , MqttMessage msg ) throws Exception;

    void registerProcessor(MqttMessageType mqttMessageType, RemotingProcessor<?> processor);

    void registerDefaultExecutor(ExecutorService executor);

    ExecutorService getDefaultExecutor();
}