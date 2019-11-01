package com.hailin.iot.route.service.impl;

import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.contanst.Contants;
import com.hailin.iot.common.util.BrokerUtil;
import com.hailin.iot.route.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate redisTemplate;
//    private LettuceConnectionFactory lettuceConnectionFactory;

    @Override
    public List<Broker> getAllBroker() {
        long now = System.currentTimeMillis();
        Set<byte[]> bytes = redisTemplate.opsForZSet().rangeByScore(Contants.REDIS_BROKER_KEY.getBytes() , now - Contants.BROKER_FIRE_TIME - 1000  , now );
//        Set<byte[]> bytes = lettuceConnectionFactory.getConnection().zRangeByScore(Contants.REDIS_BROKER_KEY.getBytes() , now - Contants.BROKER_FIRE_TIME - 1000  , now );
        return BrokerUtil.deSerializationToObj(bytes);
    }


    @Override
    public void setBrokerInfoToRedis(Broker broker) {
        long now = System.currentTimeMillis();
//        lettuceConnectionFactory.getConnection().zSetCommands().zAdd(Contants.REDIS_BROKER_KEY.getBytes() , now , BrokerUtil.serializeToByteArray(broker) );
        redisTemplate.opsForZSet().add(Contants.REDIS_BROKER_KEY.getBytes()  , BrokerUtil.serializeToByteArray(broker) , now);
    }

}
