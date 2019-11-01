package com.hailin.iot.common.remoting;

import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.concurrent.Executor;

/**
 * 处理逻辑processor接口
 * @param <T>
 */
public interface UserProcessor<T> {

    BizContext preHandleRequest(RemotingContext remotingContext , T request);

    void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, T request);

    Object handleRequest(BizContext bizContext , T request) throws Exception;

    String interst();

    Executor getExecutor();

    boolean processInIOThread();

    boolean timeoutDiscard();

    void SetExecutorSelector( ExecutorSelector executorSelector);

    ExecutorSelector getExecutorSelector();

    interface ExecutorSelector {
        Executor select(MqttMessageType messageType, Object requestHeader);
    }
}
