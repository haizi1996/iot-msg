package com.hailin.iot.route.config;

import com.hailin.iot.common.service.RedisService;
import com.hailin.iot.common.service.impl.RedisServiceImpl;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * redis配置文件
 * @author hailin
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public RedisService createRedisInstance() {
        return new RedisServiceImpl();
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }
}
