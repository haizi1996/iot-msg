package com.hailin.iot.remoting.processor;

import com.hailin.iot.remoting.RemotingContext;
import io.netty.handler.codec.mqtt.MqttMessage;

import java.util.concurrent.ExecutorService;

/**
 * 远程处理接口
 * @author zhanghaili
 *
 */
public interface RemotingProcessor<T extends MqttMessage> {

    void process(RemotingContext ctx, T msg, ExecutorService defaultExecutor) throws Exception;

    /**
     * Get the executor.
     *
     * @return
     */
    ExecutorService getExecutor();

    /**
     * Set executor.
     *
     * @param executor
     */
    void setExecutor(ExecutorService executor);
}
