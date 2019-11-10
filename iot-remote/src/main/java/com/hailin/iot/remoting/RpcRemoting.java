package com.hailin.iot.remoting;

import com.hailin.iot.common.exception.RemotingException;
import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.util.MessageUtil;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.connection.ConnectionPool;
import com.hailin.iot.remoting.factory.ResponseFactory;
import com.hailin.iot.remoting.future.InvokeFuture;
import com.hailin.iot.remoting.util.RemotingUtil;
import com.hailin.iot.remoting.util.TimerHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class RpcRemoting extends BaseRemoting {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRemoting.class);

    protected RemotingAddressParser addressParser;

    @Getter
    protected ConnectionManager connectionManager;


    public RpcRemoting( RemotingAddressParser addressParser, ConnectionManager connectionManager) {
        this.addressParser = addressParser;
        this.connectionManager = connectionManager;
    }

    public abstract void invokeWithCallback(final Url url, final Message message,
                                            final InvokeContext invokeContext,
                                            final InvokeCallback invokeCallback,
                                            final int timeoutMillis) throws RemotingException,
            InterruptedException;

    protected void invokeWithCallback(final Connection conn, final Message message,
                                      final InvokeCallback invokeCallback, final int timeoutMillis) {

        ConnectionPool connectionPool = conn.getConnectionManager().getConnectionPool(conn.getUserName());
        int messageId = connectionPool.getMessageId();
        MqttPublishMessage mqttPublishMessage = buildMqttMessage(message , messageId);

        final InvokeFuture future = createInvokeFuture(connectionPool.get(), messageId , mqttPublishMessage, invokeCallback);
        connectionPool.addInvokeFuture(future);
        final int requestId = future.invokeId();
        try {
            Timeout timeout = TimerHolder.getTimer().newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    InvokeFuture future = connectionPool.removeInvokeFuture(requestId);
                    if (future != null) {
                        future.tryAsyncExecuteInvokeCallbackAbnormally();
                    }
                }

            }, timeoutMillis, TimeUnit.MILLISECONDS);
            future.addTimeout(timeout);
            conn.getChannel().writeAndFlush(mqttPublishMessage).addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture cf) throws Exception {
                    if (!cf.isSuccess()) {
                        InvokeFuture f = connectionPool.removeInvokeFuture(requestId);
                        if (f != null) {
                            f.cancelTimeout();
                            f.tryAsyncExecuteInvokeCallbackAbnormally();
                        }
                        log.error("Invoke send failed. The address is {}",
                                RemotingUtil.parseRemoteAddress(conn.getChannel()), cf.cause());
                    }
                }

            });
        } catch (Exception e) {
            InvokeFuture f = connectionPool.removeInvokeFuture(requestId);
            if (f != null) {
                f.cancelTimeout();
                f.tryAsyncExecuteInvokeCallbackAbnormally();
            }
            log.error("Exception caught when sending invocation. The address is {}",
                    RemotingUtil.parseRemoteAddress(conn.getChannel()), e);
        }
    }

    protected abstract InvokeFuture createInvokeFuture(Connection connection , int messageId, MqttMessage request, InvokeCallback invokeCallback);

    protected abstract void preProcessInvokeContext(InvokeContext invokeContext,
                                                    Message message, Connection connection);
    /**
     * 构建mqtt消息
     * @param message
     * @return
     */
    private MqttPublishMessage buildMqttMessage(Message message , int messageId) {
        ByteBuf byteBuf = new PooledByteBufAllocator().directBuffer();
        return MqttMessageBuilders.publish().qos(MqttQoS.AT_LEAST_ONCE)
                .topicName(message.getAcceptUser())
                .retained(false)
                .messageId(messageId)
                .payload(byteBuf.writeBytes(MessageUtil.serializeToByteArray(message)))
                .build();

    }
}
