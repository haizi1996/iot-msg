package com.hailin.iot.broker.service;

import com.hailin.iot.common.cache.BrokerCacheInstance;
import com.hailin.iot.common.contanst.Contants;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.service.LoadBalance;
import com.hailin.iot.common.service.impl.ConsistentHashLoadBalancer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Objects;

public class DubboLoadBalance extends AbstractLoadBalance {

    private LoadBalance loadBalance = new ConsistentHashLoadBalancer();

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        String accepterUser = invocation.getArguments()[0].toString();

        if(CollectionUtils.isEmpty(invokers)){
            return null;
        }
        String broker_ip = invocation.getAttachment(Contants.BROKER_IP);
        Broker broker = null;
        if(StringUtils.isEmpty(broker_ip)){
            broker = loadBalance.select(BrokerCacheInstance.getInstance(null).getAllBrokerInfos() , accepterUser);
        }else {
            int port = Integer.parseInt(invocation.getAttachment(Contants.BROKER_PORT , "0"));
            broker = Broker.builder().ip(broker_ip).port(port).build();
        }
        if(Objects.isNull(broker)){
            return null;
        }
        for (Invoker<T> invoker : invokers) {
            if(isTargetIpAddress(broker , invoker)){
                return invoker;
            }
        }
        return null;
    }

    private <T> boolean isTargetIpAddress(Broker broker, Invoker<T> invoker) {
        return Objects.equals(invoker.getUrl().getAddress() , broker.getIp()) && invoker.getUrl().getPort() == broker.getPort();
    }
}
