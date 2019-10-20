package com.hailin.iot.common.remoting.factory.impl;

import com.hailin.iot.common.remoting.codec.Codec;
import com.hailin.iot.common.remoting.config.configs.ConfigurableInstance;
import io.netty.channel.ChannelHandler;

public class DefaultConnectionFactory extends AbstractConnectionFactory{

    public DefaultConnectionFactory(Codec codec, ChannelHandler heartbeatHandler,
                                    ChannelHandler handler, ConfigurableInstance configInstance) {
        super(codec, heartbeatHandler, handler, configInstance);
    }



}
