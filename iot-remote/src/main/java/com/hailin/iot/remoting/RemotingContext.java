package com.hailin.iot.remoting;

import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.util.ConnectionUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class RemotingContext {

    private ChannelHandlerContext channelContext;

    private boolean serverSide = false;

    //当处理请求超时时，请求将会被抛弃
    private boolean timeoutDiscard = true;

    //请求达到时的时间戳
    private long arriveTimestamp;

    //消息类型
    private MqttMessageType type;

    //请求超时时间
    private int timeout;

    private InvokeContext invokeContext;

    private ConcurrentHashMap<MqttMessageType , UserProcessor<?>> userProcessors;

    public RemotingContext setTimeoutDiscard(boolean failFastEnabled) {
        this.timeoutDiscard = failFastEnabled;
        return this;
    }
    public RemotingContext(ChannelHandlerContext ctx, InvokeContext invokeContext,
                           boolean serverSide,
                           ConcurrentHashMap<MqttMessageType, UserProcessor<?>> userProcessors) {
        this.channelContext = ctx;
        this.serverSide = serverSide;
        this.arriveTimestamp = System.currentTimeMillis();
        this.userProcessors = userProcessors;
        this.invokeContext = invokeContext;
    }
    public ChannelFuture writeAndFlush(MqttMessage msg) {
        return this.channelContext.writeAndFlush(msg);
    }

    /**
     * Get user processor for messageType.
     *
     * @param messageType 消息类型
     * @return
     */
    public UserProcessor<?> getUserProcessor(MqttMessageType messageType) {
        return messageType == null ? null : this.userProcessors.get(messageType);
    }

    public boolean isRequestTimeout() {
        if (this.timeout > 0 && (this.type != MqttMessageType.PUBLISH)
                && (System.currentTimeMillis() - this.arriveTimestamp) > this.timeout) {
            return true;
        }
        return false;
    }

    public Connection getConnection() {
        return ConnectionUtil.getConnectionFromChannel(channelContext.channel());
    }
}
