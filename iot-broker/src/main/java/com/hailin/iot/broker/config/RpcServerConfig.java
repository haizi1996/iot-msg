package com.hailin.iot.broker.config;

import com.hailin.iot.broker.remoting.RpcServer;
import com.hailin.iot.remoting.UserProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class RpcServerConfig {


    @Autowired
    private ConfigValue configValue;

    @Autowired
    private UserProcessor mqttConnectUserProcessor;

    @Autowired
    private UserProcessor mqttPublishUserProcessor;

    @Bean
    public RpcServer newRpcServer(){
        RpcServer rpcServer = new RpcServer( configValue.getPort());
        rpcServer.registerUserProcessor(mqttConnectUserProcessor);
        rpcServer.registerUserProcessor(mqttPublishUserProcessor);
        return rpcServer;
    }

}
