package com.hailin.iot.common.remoting;

import com.hailin.iot.common.remoting.processor.AbstractRemotingProcessor;
import io.netty.handler.codec.mqtt.MqttMessage;

import java.util.concurrent.atomic.AtomicBoolean;

public class RpcAsyncContext implements AsyncContext {

    private RemotingContext ctx;

    private AbstractRemotingProcessor processor;

    private MqttMessage requestMessage;

    private AtomicBoolean isResponseSentAlready = new AtomicBoolean();


    public RpcAsyncContext(final RemotingContext ctx,  MqttMessage requestMessage , final AbstractRemotingProcessor processor) {
        this.ctx = ctx;
        this.requestMessage = requestMessage;
        this.processor = processor;
    }


    @Override
    public void sendResponse(MqttMessage message) {
        if (isResponseSentAlready.compareAndSet(false, true)) {
            processor.sendResponseIfNecessary(this.ctx, message.fixedHeader().messageType(), message);
        } else {
            throw new IllegalStateException("Should not send rpc response repeatedly!");
        }
    }
}
