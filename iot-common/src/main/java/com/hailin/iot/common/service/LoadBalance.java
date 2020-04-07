package com.hailin.iot.common.service;


import com.hailin.iot.common.model.Broker;

import java.util.List;

/**
 * 负载均衡算法
 * @author zhanghailin
 */
public interface LoadBalance {

    Broker select(List<Broker> servers, String username);
}
