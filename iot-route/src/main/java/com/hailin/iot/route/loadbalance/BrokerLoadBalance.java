package com.hailin.iot.route.loadbalance;

import com.hailin.iot.common.model.Broker;

import java.util.List;

/**
 * broker负载策略
 * @author hailin
 */
public interface BrokerLoadBalance {


    Broker loadBalance(List<Broker> brokers);

}
