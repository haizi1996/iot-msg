package com.hailin.iot.common.cache;

import com.google.common.collect.Lists;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
public class BrokerCacheInstance {

    private static volatile BrokerCacheInstance instance;

    private static final Long EXPIRE_TIME = 1 * 60 * 60 * 1000L;

    private BrokerCacheInstance(){}

    private static Object lock = new Object();
    /**
     * key --> token
     * value --> 用户信息
     */
    private ConcurrentSkipListSet<Broker > cache = new ConcurrentSkipListSet<>();

    private volatile long size ;


    public static BrokerCacheInstance getInstance(RedisService redisService){
        if(Objects.isNull(instance)) {
            synchronized (lock) {
                if (Objects.isNull(instance)) {
                    instance = new BrokerCacheInstance();
                    instance.size = redisService.getSize();
                    long end = System.currentTimeMillis();
                    instance.cache.addAll(redisService.getAddBroker(end));
                }
            }
        }
        return instance;
    }

    public void put(List<Broker> brokerInfos){
        cache.addAll(brokerInfos);
    }

    public List<Broker> getAllBrokerInfos(){
        boolean flag = true;
        long now = System.currentTimeMillis();
        while (flag && CollectionUtils.isNotEmpty(cache)){
            Broker broker = cache.last();
            if (now - broker.getScore() >= EXPIRE_TIME){
                cache.pollLast();
            }else {
                flag = false;
            }
        }
        return Lists.newArrayList(cache);
    }



}
