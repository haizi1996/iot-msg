package com.hailin.iot.remoting;

import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

public class RpcAsyncContext<T extends MqttMessage> implements AsyncContext {

    private RemotingContext ctx;

    private AbstractRemotingProcessor processor;

    @Getter
    private T message;

    private AtomicBoolean isResponseSentAlready = new AtomicBoolean();


    public RpcAsyncContext(final RemotingContext ctx, T message, final AbstractRemotingProcessor processor) {
        this.ctx = ctx;
        this.message = message;
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
