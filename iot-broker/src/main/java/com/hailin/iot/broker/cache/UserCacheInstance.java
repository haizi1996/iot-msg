package com.hailin.iot.broker.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hailin.iot.common.model.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;


/**
 * 用户缓存实例
 * @author hailin
 */
@NoArgsConstructor
@Slf4j
public class UserCacheInstance {

    /**
     * key --> token
     * value --> 用户信息
     */
    private static Cache<String, User> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .initialCapacity(10000)
            .recordStats()
            .build();

    public static User get(String userName){
        return cache.getIfPresent(userName);
    }

    public static User get(String userName , Callable cacheLoader ){
        try {
            return cache.get(userName , cacheLoader);
        } catch (ExecutionException e) {
            log.error("" , e);
            return null;
        }
    }

    public static void delete(String userName){
        cache.invalidate(userName);
    }

}
