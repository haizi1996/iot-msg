package com.hailin.iot.broker.remoting;

import com.hailin.iot.remoting.UserProcessor;
import com.hailin.iot.remoting.codec.impl.MqttCoder;
import com.hailin.iot.remoting.config.configs.ConfigurableInstance;
import com.hailin.iot.remoting.factory.impl.DefaultConnectionFactory;
import com.hailin.iot.remoting.handler.HeartbeatHandler;
import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.concurrent.ConcurrentHashMap;

public class MqttConnectionFactory extends DefaultConnectionFactory {
    public MqttConnectionFactory(ConcurrentHashMap<MqttMessageType, UserProcessor<?>> userProcessors, ConfigurableInstance confInstance) {
        super(new MqttCoder(), new HeartbeatHandler(), new RpcHandler(userProcessors),
                confInstance);
    }
}
