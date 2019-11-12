package com.hailin.iot.broker.config;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableDubbo(scanBasePackages = "com.hailin.iot.broker.service.provider")
@PropertySource("classpath:/dubbo-provider.properties")
public class ProviderConfiguration {
}
