package com.hailin.iot.broker.service;

import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.loadbalance.RandomLoadBalance;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


import static com.hailin.iot.common.contanst.Contants.USER_ONLINE;

public class DubboLoadBalance extends RandomLoadBalance {
    @Setter
    private RedisTemplate redisTemplate;

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        String accepterUser = invocation.getArguments()[0].toString();

        if(CollectionUtils.isEmpty(invokers)){
            return null;
        }
        String ipAddress =  new String((byte[])redisTemplate.opsForHash().get(USER_ONLINE.getBytes() , accepterUser.getBytes()));
        List<Invoker<T>> invokerList =  invokers.stream().filter(invoker -> isTargetIpAddress(ipAddress , invoker)).collect(Collectors.toList());
        return invokerList.get(0);
    }

    private <T> boolean isTargetIpAddress(String ipAddress, Invoker<T> invoker) {
        return Objects.equals(invoker.getUrl().getAddress() , ipAddress);
    }
}
