package com.hailin.iot.remoting;

import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.concurrent.Executor;

/**
 * 处理逻辑processor接口
 * @param <T>
 */
public interface UserProcessor<T extends MqttMessage> {

    BizContext preHandleRequest(RemotingContext remotingContext , T request);

    void handleRequest(BizContext bizCtx, AsyncContext asyncCtx);

    Object handleRequest(BizContext bizContext , T request) throws Exception;

    Executor getExecutor();


    boolean timeoutDiscard();

    MqttMessageType interest();

}
