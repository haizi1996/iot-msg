package com.hailin.iot.route.controller;

import com.hailin.iot.common.cache.BrokerCacheInstance;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.model.User;
import com.hailin.iot.common.dto.BrokerInfo;
import com.hailin.iot.common.service.LoadBalance;
import com.hailin.iot.common.service.impl.ConsistentHashLoadBalancer;
import com.hailin.iot.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

/**
 * 用户登录的模块
 * @author zhanghailin
 *
 */
@RestController
@RequestMapping("/iot-msg")
public class UserController {

    @Resource
    private UserService userService;

    private LoadBalance loadBalance = new ConsistentHashLoadBalancer();

    @PostMapping("/user/login")
    public Broker login(@NotEmpty  String userName){
        User user = userService.findByUserName(userName);
        return loadBalance.select(BrokerCacheInstance.getInstance(null).getAllBrokerInfos(), userName);
    }

    @PostMapping("/user/register")
    public BrokerInfo registerUser(User user){
        Long num = userService.saveUser(user);
        if(num > 0){

        }
        return null;
    }
}
