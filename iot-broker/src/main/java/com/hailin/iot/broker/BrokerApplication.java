package com.hailin.iot.broker;


import com.hailin.iot.broker.remoting.RpcServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.PreDestroy;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@MapperScan(basePackages = {"com.hailin.iot.broker.user.dao"})
@ComponentScan(basePackages = {"com.hailin.iot"})
@ImportResource("classpath:/dubbo.xml")
public class BrokerApplication {

    @Autowired
    private RpcServer rpcServer ;


    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = SpringApplication.run(BrokerApplication.class , args);
        RpcServer rpcServer = applicationContext.getBean(RpcServer.class);
        rpcServer.startup();
        System.out.println("dubbo service started");
        new CountDownLatch(1).await();
    }



    @PreDestroy
    public void close(){
//        RpcServer rpcServer = applicationContext.getBean(RpcServer.class);
        rpcServer.shutdown();
    }

}
