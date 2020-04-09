package com.hailin.iot.broker.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
    private static Cache<String, UserCache> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())
            .initialCapacity(10000)
            .recordStats()
            .build();

    public static UserCache get(String userName){
        return cache.getIfPresent(userName);
    }

    public static UserCache get(String userName , Callable cacheLoader ){
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

    public static void put(UserCache userCache){
        cache.put(userCache.getUserName() , userCache);
    }

}
