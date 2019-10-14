package com.hailin.iot.route.loadbalance;

import com.hailin.iot.common.Broker;
import com.hailin.iot.common.protoc.BrokerBuf;

import java.util.List;

/**
 * broker负载策略
 * @author hailin
 */
public interface BrokerLoadBalance {


    Broker loadBalance(List<Broker> brokers);

}
