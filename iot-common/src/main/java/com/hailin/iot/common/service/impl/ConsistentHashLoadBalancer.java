package com.hailin.iot.common.service.impl;

import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.service.HashStrategy;
import com.hailin.iot.common.service.LoadBalance;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性hash负载均衡
 */
public class ConsistentHashLoadBalancer implements LoadBalance {

    private HashStrategy hashStrategy = new FnvHashStrategy();

    private final static int VIRTUAL_NODE_SIZE = 1;
    private final static String VIRTUAL_NODE_SUFFIX = "&&";

    @Override
    public Broker select(List<Broker> brokerInfos, String username) {
        int invocationHashCode = hashStrategy.getHashCode(username);
        TreeMap<Integer, Broker> ring = buildConsistentHashRing(brokerInfos);
        Broker brokerInfo = locate(ring, invocationHashCode);
        return brokerInfo;
    }

    private TreeMap<Integer, Broker> buildConsistentHashRing(List<Broker> brokerInfos) {
        TreeMap<Integer, Broker> virtualNodeRing = new TreeMap<>();
        for (Broker brokerInfo : brokerInfos) {
            for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
                // 新增虚拟节点的方式如果有影响，也可以抽象出一个由物理节点扩展虚拟节点的类
                virtualNodeRing.put(hashStrategy.getHashCode(brokerInfo.getUrl() + VIRTUAL_NODE_SUFFIX + i), brokerInfo);
            }
        }
        return virtualNodeRing;
    }

    private Broker locate(TreeMap<Integer, Broker> ring, int invocationHashCode) {
        // 向右找到第一个 key
        Map.Entry<Integer, Broker> locateEntry = ring.ceilingEntry(invocationHashCode);
        if (locateEntry == null) {
            // 想象成一个环，超过尾部则取第一个 key
            locateEntry = ring.firstEntry();
        }
        return locateEntry.getValue();
    }

}
