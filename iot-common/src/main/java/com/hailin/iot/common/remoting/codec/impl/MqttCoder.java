package com.hailin.iot.common.remoting.codec.impl;

import com.hailin.iot.common.remoting.codec.Codec;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;

/**
 * mqtt协议的编码器
 */
public class MqttCoder implements Codec {

    @Override
    public ChannelHandler newEncoder() {
        return MqttEncoder.INSTANCE;
    }

    @Override
    public ChannelHandler newDecoder() {
        return new MqttDecoder();
    }
}
