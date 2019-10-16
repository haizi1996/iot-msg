package com.hailin.iot.common.remoting.codec;

import io.netty.channel.ChannelHandler;

/**
 * 编解码接口
 * @author hailin
 */
public interface Codec {

    /**
     * 创建一个编码的 channelHandler
     */
    ChannelHandler newEncoder();

    /**
     * 创建解码的channelHandler
     */
    ChannelHandler newDecoder();
}
