package com.hailin.iot.common.remoting;

import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * 异步上下文接口
 * @author hailin
 */
public interface AsyncContext {

    /**
     * 发送请求回调
     * @param message 请求的结果集
     */
    void sendResponse(MqttMessage message);

}
