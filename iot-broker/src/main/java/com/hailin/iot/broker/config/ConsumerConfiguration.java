package com.hailin.iot.broker.config;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableDubbo(scanBasePackages = "com.hailin.iot.common.rpc")
@PropertySource("classpath:/dubbo-consumer.properties")
@ComponentScan(value = {"com.hailin.iot.common.rpc"})
public class ConsumerConfiguration {
}
