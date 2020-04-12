package com.hailin.iot.route.service;

import com.hailin.iot.common.contanst.Contants;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.service.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisServiceTest {

    @Resource
    private RedisService redisService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void getAllBroker() {
        List<Broker> brokers = redisService.getAddBroker(System.currentTimeMillis());
        for (Broker broker : brokers) {
            System.out.println(broker.toString());
        }

    }


    @Test
    public void setBrokerInfoToRedis() {
        redisTemplate.delete(Contants.REDIS_BROKER_KEY.getBytes());
//        int i = 0;
//        while (i ++ < 10){
//            Broker broker = Broker.builder().ip("localhost" + i).port(3030).score(System.currentTimeMillis()).build();
//            redisService.setBrokerInfoToRedis(broker);
//        }
//        getAllBroker();
    }
}