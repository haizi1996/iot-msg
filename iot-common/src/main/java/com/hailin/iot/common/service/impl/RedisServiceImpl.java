package com.hailin.iot.common.service.impl;

import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.contanst.Contants;
import com.hailin.iot.common.util.BrokerUtil;
import com.hailin.iot.common.service.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate redisTemplate;
//    private LettuceConnectionFactory lettuceConnectionFactory;


    @Override
    public long getSize() {
        return redisTemplate.opsForZSet().size(Contants.REDIS_BROKER_KEY.getBytes());
    }

    @Override
    public List<Broker> getAddBroker(long end , long range) {
        Set<ZSetOperations.TypedTuple<byte[]>> typedTuples = redisTemplate.opsForZSet().rangeByScoreWithScores(Contants.REDIS_BROKER_KEY.getBytes() , end - range   , end );
        if (CollectionUtils.isEmpty(typedTuples)){
            return Collections.EMPTY_LIST;
        }
        return typedTuples.stream().map( typedTuple -> Objects.requireNonNull(BrokerUtil.deSerializationToObj(typedTuple.getValue())).setScore(Math.round(typedTuple.getScore()))).collect(Collectors.toList());
    }

    @Override
    public List<Broker> getAddBroker(long end) {
        return getAddBroker(end ,Contants.BROKER_FIRE_TIME );
    }

    @Override
    public void setBrokerInfoToRedis(Broker broker) {
        long now = System.currentTimeMillis();
//        lettuceConnectionFactory.getConnection().zSetCommands().zAdd(Contants.REDIS_BROKER_KEY.getBytes() , now , BrokerUtil.serializeToByteArray(broker) );
        redisTemplate.opsForZSet().add(Contants.REDIS_BROKER_KEY.getBytes()  , BrokerUtil.serializeToByteArray(broker) , now);
    }

}
