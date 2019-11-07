package com.hailin.iot.remoting;

import io.netty.channel.ChannelHandlerContext;

/**
 * 心跳触发的接口
 * @author hailin
 */
public interface HeartbeatTrigger {

    void heartbeatTriggered(final ChannelHandlerContext context) throws Exception;
}
