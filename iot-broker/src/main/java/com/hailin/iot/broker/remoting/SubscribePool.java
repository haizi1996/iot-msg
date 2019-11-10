package com.hailin.iot.broker.remoting;

import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订阅池
 * @author hailin
 */
@NoArgsConstructor

public class SubscribePool {

    private static final SubscribePool INSTANCE = new SubscribePool();

    private  Map<String , String> topic2PoolKey = new ConcurrentHashMap<>();

    public static SubscribePool getInstance(){
        return INSTANCE;
    }

    public String getPoolKey(String topicName){
        return topic2PoolKey.get(topicName);
    }

    public void register(String topicName , String poolKey){
        topic2PoolKey.put(topicName , poolKey);
    }

    public void unSubscribe(String topicName){
        topic2PoolKey.remove(topicName);
    }

}
