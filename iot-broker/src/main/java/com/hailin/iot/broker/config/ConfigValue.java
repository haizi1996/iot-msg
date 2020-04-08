package com.hailin.iot.broker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ConfigValue {

    @Value("${netty.port}")
    private int port;


}
