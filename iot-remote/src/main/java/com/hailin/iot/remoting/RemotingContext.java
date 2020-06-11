package com.hailin.iot.remoting;

import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.util.ConnectionUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class RemotingContext {

    private final Long id;

    private ChannelHandlerContext channelContext;

    private boolean serverSide = false;

    //请求达到时的时间戳
    private long arriveTimestamp;

    //消息类型
    private MqttMessageType type;

    private InvokeContext invokeContext;

    private ConnectionManager connectionManager;

    private ConcurrentHashMap<MqttMessageType , UserProcessor<?>> userProcessors;

    public RemotingContext(ChannelHandlerContext ctx, InvokeContext invokeContext,
                           ConnectionManager connectionManager,
                           boolean serverSide,
                           ConcurrentHashMap<MqttMessageType, UserProcessor<?>> userProcessors) {
        this.channelContext = ctx;
        this.connectionManager = connectionManager;
        this.serverSide = serverSide;
        this.arriveTimestamp = System.currentTimeMillis();
        this.userProcessors = userProcessors;
        this.invokeContext = invokeContext;
        this.id = ctx.channel().attr(Connection.MESSAGE_ID).get().incrementAndGet();
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



    public Connection getConnection() {
        return ConnectionUtil.getConnectionFromChannel(channelContext.channel());
    }
}
