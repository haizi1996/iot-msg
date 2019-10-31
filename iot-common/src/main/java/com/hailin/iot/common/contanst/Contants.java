package com.hailin.iot.common.contanst;

import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 一些常量信息
 * @author hailin
 */
public interface Contants {

    String REDIS_BROKER_KEY = "redisBrokerKey";

    long BROKER_FIRE_TIME = 5 * 1000;

    String IDLE_STATE_HANDLER = "idleStateHandler";

    String IDLE_HANDLER = "IdleHandler";

    String PROTOCOL_NAME = "MQTT";

    List<Integer> SUPPORT_PROTOCOL_VERSION = Lists.newArrayList(3);
}
