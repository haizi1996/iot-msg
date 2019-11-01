package com.hailin.iot.route.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Test
    public void getAllBroker() {

//        List<Broker> brokers = redisService.getAllBroker();
//        for (Broker broker :
//                brokers) {
//            System.out.printf(broker.toString());
//        }

        int i= 'n' - 'a' ;
        System.out.println(i);
    }


    @Test
    public void setBrokerInfoToRedis() {
//        Broker broker = Broker.builder().host("localhost").port(3030).build();
//        redisService.setBrokerInfoToRedis(broker);
//        getAllBroker();
    }
}