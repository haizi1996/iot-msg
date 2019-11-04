package com.hailin.iot.broker.task;

import com.hailin.iot.broker.util.IpUtils;
import com.hailin.iot.common.contanst.Contants;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.util.BrokerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RegisterBroker {

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${netty.port}")
    private List<Integer> ports;

    @Scheduled(fixedRate = 5000) //每5秒执行一次
    public void register(){
        if (CollectionUtils.isEmpty(ports)){
            return;
        }
        log.debug("register broker is started!");
        long now = System.currentTimeMillis();
        Set<ZSetOperations.TypedTuple> brokers = ports.stream()
        .map(port -> Broker.builder().host(IpUtils.getLocalIpAddress()).port(port).build())
                .map(broker -> new DefaultTypedTuple(BrokerUtil.serializeToByteArray(broker) , new Double(now)))
                .collect(Collectors.toSet());
        redisTemplate.opsForZSet().add(Contants.REDIS_BROKER_KEY.getBytes() , brokers);
    }

}