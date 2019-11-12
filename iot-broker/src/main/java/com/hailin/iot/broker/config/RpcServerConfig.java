package com.hailin.iot.broker.config;

import com.hailin.iot.broker.remoting.RpcServer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcServerConfig {


    @Value("${netty.port}")
    private Integer port;

    @Bean
    public RpcServer newRpcServer(){
        return new RpcServer( port);
    }

}
