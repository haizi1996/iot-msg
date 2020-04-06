package com.hailin.iot.remoting;

import com.hailin.iot.common.exception.RemotingException;
import com.hailin.iot.common.model.Message;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.future.InvokeFuture;
import io.netty.handler.codec.mqtt.MqttMessage;

public class RpcClientRemoting extends RpcRemoting {
    public RpcClientRemoting( RemotingAddressParser addressParser,
                             DefaultConnectionManager connectionManager) {
        super( addressParser, connectionManager);
    }

    @Override
    public void invokeWithCallback(Url url, Message message, InvokeContext invokeContext, InvokeCallback invokeCallback, int timeoutMillis) throws RemotingException, InterruptedException {

    }

    @Override
    protected InvokeFuture createInvokeFuture(Connection connection, int messageId, MqttMessage request, InvokeCallback invokeCallback) {
        return null;
    }

    @Override
    protected void preProcessInvokeContext(InvokeContext invokeContext, Message message, Connection connection) {

    }
}
