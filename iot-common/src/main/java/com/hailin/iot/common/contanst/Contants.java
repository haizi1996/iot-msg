package com.hailin.iot.common.contanst;

import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 一些常量信息
 * @author hailin
 */
public interface Contants {

    String REDIS_BROKER_KEY = "redisBrokerKey";
    String REDIS_USER_KEY = "redisUserKey";

    String BROKER= "broker";

    long BROKER_FIRE_TIME = 5 * 1000;

    String IDLE_STATE_HANDLER = "idleStateHandler";

    String IDLE_HANDLER = "IdleHandler";

    String USER_ONLINE = "user_online";

}
