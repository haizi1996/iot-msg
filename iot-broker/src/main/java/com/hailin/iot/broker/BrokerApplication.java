package com.hailin.iot.broker;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo(scanBasePackages = {"com.hailin.iot.broker.service.provider"})
public class BrokerApplication  {

    public static void main(String[] args) {
        SpringApplication.run(BrokerApplication.class , args);
    }

}
