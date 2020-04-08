package com.hailin.iot.remoting.connection;

import com.hailin.iot.remoting.ConnectionManager;
import com.hailin.iot.remoting.RpcHandler;
import com.hailin.iot.remoting.UserProcessor;
import com.hailin.iot.remoting.codec.impl.MqttCoder;
import com.hailin.iot.remoting.config.configs.ConfigurableInstance;
import com.hailin.iot.remoting.factory.impl.DefaultConnectionFactory;
import com.hailin.iot.remoting.handler.HeartbeatHandler;
import com.hailin.iot.remoting.handler.MessageHandler;
import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.concurrent.ConcurrentHashMap;

public class MqttConnectionFactory extends DefaultConnectionFactory {
    public MqttConnectionFactory(ConcurrentHashMap<MqttMessageType, UserProcessor<?>> userProcessors , ConnectionManager connectionManager, ConfigurableInstance confInstance , MessageHandler messageHandler) {
        super(new MqttCoder(), new HeartbeatHandler(), new RpcHandler(connectionManager , userProcessors  , messageHandler),
                confInstance);
    }
}
