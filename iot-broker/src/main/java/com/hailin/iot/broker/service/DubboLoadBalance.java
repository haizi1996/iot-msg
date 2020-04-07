package com.hailin.iot.broker.service;

import com.hailin.iot.common.cache.BrokerCacheInstance;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.service.LoadBalance;
import com.hailin.iot.common.service.impl.ConsistentHashLoadBalancer;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


import static com.hailin.iot.common.contanst.Contants.USER_ONLINE;

public class DubboLoadBalance extends AbstractLoadBalance {
    @Setter
    private RedisTemplate redisTemplate;

    private LoadBalance loadBalance = new ConsistentHashLoadBalancer();

//    @Override
//    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
//        String accepterUser = invocation.getArguments()[0].toString();
//
//        if(CollectionUtils.isEmpty(invokers)){
//            return null;
//        }
//        String ipAddress =  new String((byte[])redisTemplate.opsForHash().get(USER_ONLINE.getBytes() , accepterUser.getBytes()));
//        List<Invoker<T>> invokerList =  invokers.stream().filter(invoker -> isTargetIpAddress(ipAddress , invoker)).collect(Collectors.toList());
//        return doSelect(invokerList , url , invocation);
//    }


    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        String accepterUser = invocation.getArguments()[0].toString();

        if(CollectionUtils.isEmpty(invokers)){
            return null;
        }
        String ipAddress =  new String((byte[])redisTemplate.opsForHash().get(USER_ONLINE.getBytes() , accepterUser.getBytes()));
        Broker broker = loadBalance.select(BrokerCacheInstance.getInstance(null).getAllBrokerInfos() , accepterUser);



        List<Invoker<T>> invokerList =  invokers.stream().filter(invoker -> isTargetIpAddress(ipAddress , invoker)).collect(Collectors.toList());
        return null;
    }

    private <T> boolean isTargetIpAddress(String ipAddress, Invoker<T> invoker) {
        return Objects.equals(invoker.getUrl().getAddress() , ipAddress);
    }
}
