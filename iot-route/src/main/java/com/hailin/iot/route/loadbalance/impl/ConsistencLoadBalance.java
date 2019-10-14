package com.hailin.iot.route.loadbalance.impl;

import com.hailin.iot.common.Broker;
import com.hailin.iot.route.loadbalance.BrokerLoadBalance;

import java.util.List;

/**
 * 一致性Hash服务
 */
public class ConsistencLoadBalance implements BrokerLoadBalance {
    @Override
    public Broker loadBalance(List<Broker> brokers) {
        return null;
    }
}
