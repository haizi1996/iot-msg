package com.hailin.iot.broker;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@MapperScan(basePackages = {"com.hailin.iot.user.dao"})
@ComponentScan(basePackages = {"com.hailin.iot"})
@EnableDubbo(scanBasePackages = {"com.hailin.iot.broker.service.provider" , "com.hailin.iot.common.rpc"})
public class BrokerApplication  {

    public static void main(String[] args) throws InterruptedException {
        new EmbeddedZooKeeper(2181, false).start();
        SpringApplication.run(BrokerApplication.class , args);
        System.out.println("dubbo service started");
        new CountDownLatch(1).await();
    }

}
