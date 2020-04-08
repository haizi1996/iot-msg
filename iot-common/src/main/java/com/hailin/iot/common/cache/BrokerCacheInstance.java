package com.hailin.iot.common.cache;

import com.hailin.iot.common.contanst.Contants;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
public class BrokerCacheInstance {

    private static volatile BrokerCacheInstance instance;

    private static final Long EXPIRE_TIME = 1 * 60 * 1000L;

    private BrokerCacheInstance(){}
    /**
     * key --> token
     * value --> 用户信息
     */
    private ConcurrentSkipListSet<Broker> cache = new ConcurrentSkipListSet<>(Comparator.comparingLong(Broker::getScore));

    private volatile long size ;


    public static BrokerCacheInstance getInstance(RedisService redisService){
        if(Objects.isNull(instance)) {
            synchronized (BrokerCacheInstance.class) {
                if (Objects.isNull(instance)) {
                    instance = new BrokerCacheInstance();
                    instance.size = redisService.getSize();
                    long end = System.currentTimeMillis();
                    while (instance.cache.size() < instance.size){
                        instance.cache.addAll(redisService.getAddBroker(end));
                        end -=  Contants.BROKER_FIRE_TIME;
                    }
                }
            }
        }
        return instance;
    }

    public void put(List<Broker> brokerInfos){
        cache.addAll(brokerInfos);
    }

    public List<Broker> getAllBrokerInfos(){
        if(CollectionUtils.isEmpty(cache)){
            return Collections.EMPTY_LIST;
        }
        boolean flag = true;
        long now = System.currentTimeMillis();
        while (flag){
            Broker broker = cache.last();
            if (now - broker.getScore() >= EXPIRE_TIME){
                cache.pollLast();
            }else {
                flag = true;
            }
        }
        return Lists.newArrayList(cache);
    }



}