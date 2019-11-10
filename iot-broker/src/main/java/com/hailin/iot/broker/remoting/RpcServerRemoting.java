package com.hailin.iot.broker.remoting;

import com.hailin.iot.common.exception.RemotingException;
import com.hailin.iot.remoting.ConnectionManager;
import com.hailin.iot.remoting.InvokeCallback;
import com.hailin.iot.remoting.InvokeContext;
import com.hailin.iot.remoting.RemotingAddressParser;
import com.hailin.iot.remoting.RpcInvokeCallbackListener;
import com.hailin.iot.remoting.RpcRemoting;
import com.hailin.iot.remoting.Url;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.connection.ConnectionPool;
import com.hailin.iot.remoting.future.DefaultInvokeFuture;
import com.hailin.iot.remoting.future.InvokeFuture;
import com.hailin.iot.remoting.util.RemotingUtil;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

public class RpcServerRemoting extends RpcRemoting {


    public RpcServerRemoting(RemotingAddressParser addressParser, ConnectionManager connectionManager) {
        super(addressParser, connectionManager);
    }

    @Override
    public void invokeWithCallback(Url url, MqttPublishMessage request, InvokeContext invokeContext, InvokeCallback invokeCallback, int timeoutMillis) throws RemotingException, InterruptedException {
        Connection conn = this.connectionManager.get(url.getUniqueKey());
        if (null == conn) {
            throw new RemotingException("Client address [" + url.getUniqueKey()
                    + "] not connected yet!");
        }
        this.connectionManager.check(conn);
        this.invokeWithCallback(conn, request, invokeContext, invokeCallback, timeoutMillis);
    }

    public void invokeWithCallback(final Connection conn, final MqttMessage request,
                                   final InvokeContext invokeContext,
                                   final InvokeCallback invokeCallback, final int timeoutMillis)
            throws RemotingException {
        preProcessInvokeContext(invokeContext, request, conn);
        super.invokeWithCallback(conn, request, invokeCallback, timeoutMillis);

    }

    @Override
    protected InvokeFuture createInvokeFuture(ConnectionPool connectionPool, MqttMessage request, InvokeCallback invokeCallback) {
        return new DefaultInvokeFuture(connectionPool.getMessageId(), new RpcInvokeCallbackListener(
                RemotingUtil.parseRemoteAddress(connectionPool.get().getChannel())), invokeCallback, request);
    }

    @Override
    protected void preProcessInvokeContext(InvokeContext invokeContext, MqttMessage message, Connection connection) {
        if (null != invokeContext) {
            invokeContext.putIfAbsent(InvokeContext.SERVER_REMOTE_IP,
                    RemotingUtil.parseRemoteIP(connection.getChannel()));
            invokeContext.putIfAbsent(InvokeContext.SERVER_REMOTE_PORT,
                    RemotingUtil.parseRemotePort(connection.getChannel()));
            invokeContext.putIfAbsent(InvokeContext.SERVER_LOCAL_IP,
                    RemotingUtil.parseLocalIP(connection.getChannel()));
            invokeContext.putIfAbsent(InvokeContext.SERVER_LOCAL_PORT,
                    RemotingUtil.parseLocalPort(connection.getChannel()));
            invokeContext.putIfAbsent(InvokeContext.IOT_INVOKE_REQUEST_ID, ((MqttMessageIdVariableHeader)message.variableHeader()).messageId());
        }
    }
}
