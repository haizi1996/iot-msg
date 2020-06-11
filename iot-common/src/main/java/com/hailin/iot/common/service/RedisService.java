package com.hailin.iot.common.service;

import com.hailin.iot.common.model.Broker;

import java.util.List;

/**
 * redis操作的逻辑接口
 * @author hailin
 */
public interface RedisService {



    long getSize();
    /**
     * 获取增量的broker
     * @return
     */
    List<Broker> getAddBroker(long end , long range);

    /**
     * 获取增量的broker
     * @return
     */
    List<Broker> getAddBroker(long end );

    /**
     * 将broker信息设置进redis
     * 删除过期
     * @param broker broker信息
     */
    void setBrokerInfoToRedis(Broker broker);

    /* 将broker信息设置进redis
     * 删除过期
     * @param broker broker信息
     */
    void setBrokersInfoToRedis(List<Broker> brokers);


}
