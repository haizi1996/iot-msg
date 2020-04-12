package com.hailin.iot.route.controller;

import com.hailin.iot.common.cache.BrokerCacheInstance;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.service.LoadBalance;
import com.hailin.iot.common.service.RedisService;
import com.hailin.iot.common.service.impl.ConsistentHashLoadBalancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * 用户登录的模块
 * @author zhanghailin
 *
 */
@RestController
@RequestMapping("/iot-route")
public class UserController {



    private LoadBalance loadBalance = new ConsistentHashLoadBalancer();

    @Resource
    private RedisService redisService;

    @GetMapping("/{username}")
    private Broker getBrokerByUserName(@PathVariable("username") String username){

        return loadBalance.select(BrokerCacheInstance.getInstance(redisService).getAllBrokerInfos() , username);
    }


}
