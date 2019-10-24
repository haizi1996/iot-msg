package com.hailin.iot.common.remoting;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
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

    //请求超时时间
    private int timeout;

    //rpc命令类型
    private int rpcCommandType;

    private ConcurrentHashMap<Scannable , UserProcessor<?>> userProcessors;

    public RemotingContext setTimeoutDiscard(boolean failFastEnabled) {
        this.timeoutDiscard = failFastEnabled;
        return this;
    }

    public ChannelFuture writeAndFlush(MqttMessage msg) {
        return this.channelContext.writeAndFlush(msg);
    }
}
