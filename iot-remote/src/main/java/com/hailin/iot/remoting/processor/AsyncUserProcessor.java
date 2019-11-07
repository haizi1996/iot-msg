package com.hailin.iot.remoting.processor;

import com.hailin.iot.remoting.AsyncContext;
import com.hailin.iot.remoting.BizContext;
import io.netty.handler.codec.mqtt.MqttMessage;

public abstract class AsyncUserProcessor<T> extends AbstractUserProcessor<T> {


    @Override
    public Object handleRequest(BizContext bizCtx, MqttMessage request) throws Exception {
        throw new UnsupportedOperationException(
                "SYNC handle request is unsupported in AsyncUserProcessor!");
    }


    @Override
    public abstract void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, T request);

    @Override
    public abstract String interst();
}