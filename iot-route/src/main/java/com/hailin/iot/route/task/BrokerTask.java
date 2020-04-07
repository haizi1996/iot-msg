package com.hailin.iot.route.task;

import com.hailin.iot.common.cache.BrokerCacheInstance;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.service.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class BrokerTask {

    @Resource
    private RedisService redisService;

    @Scheduled(fixedRate = 5000) //每5秒执行一次
    public void getBrokers(){
        long now = System.currentTimeMillis();
        List<Broker> brokers = redisService.getAddBroker(now , 5000L);
        if(CollectionUtils.isEmpty(brokers)){
            BrokerCacheInstance.getInstance(redisService).put(brokers);
        }
    }

}
