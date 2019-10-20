package com.hailin.iot.common.remoting.factory.impl;

import com.hailin.iot.common.remoting.RpcHandler;
import com.hailin.iot.common.remoting.UserProcessor;
import com.hailin.iot.common.remoting.codec.impl.MqttCoder;
import com.hailin.iot.common.remoting.config.configs.ConfigurableInstance;
import com.hailin.iot.common.remoting.handler.HeartbeatHandler;

import java.util.concurrent.ConcurrentHashMap;

public class MqttConnectionFactory extends DefaultConnectionFactory {
    public MqttConnectionFactory(ConcurrentHashMap<String , UserProcessor<?>> userProcessors, ConfigurableInstance confInstance) {
        super(new MqttCoder(), new HeartbeatHandler(), new RpcHandler(userProcessors),
                confInstance);
    }
}
