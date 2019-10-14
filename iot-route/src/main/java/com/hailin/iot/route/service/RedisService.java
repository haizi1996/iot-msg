package com.hailin.iot.route.service;

import com.hailin.iot.common.Broker;

import java.util.List;

/**
 * redis操作的逻辑接口
 * @author hailin
 */
public interface RedisService {

    /**
     * 获取所有Broker信息
     */
    List<Broker> getAllBroker();

    /**
     * 将broker信息设置进redis
     * @param broker broker信息
     */
    void setBrokerInfoToRedis(Broker broker);


}
