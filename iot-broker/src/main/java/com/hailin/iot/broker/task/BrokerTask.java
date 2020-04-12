package com.hailin.iot.broker.task;

import com.hailin.iot.common.util.IpUtils;
import com.hailin.iot.common.cache.BrokerCacheInstance;
import com.hailin.iot.common.contanst.Contants;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.service.RedisService;
import com.hailin.iot.common.util.BrokerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@Slf4j
public class BrokerTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisService redisService;

    @Value("${netty.port}")
    private Integer port;

    @Scheduled(fixedRate = 5000) //每5秒执行一次
    public void register(){
        if (Objects.isNull(port) || port == 0){
            return;
        }
        log.debug("register broker is started!");
        redisService.setBrokerInfoToRedis( Broker.builder().ip( IpUtils.getLocalIpAddress()).score(0L).port(port).build());
    }
    @Scheduled(fixedRate = 5000) //每5秒执行一次
    public void getBrokers(){
        long now = System.currentTimeMillis();
        List<Broker> brokers = redisService.getAddBroker(now , 5000L);
        if(CollectionUtils.isNotEmpty(brokers)){
            BrokerCacheInstance.getInstance(redisService).put(brokers);
        }
    }

}
