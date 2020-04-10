package com.hailin.iot.route.controller;

import com.hailin.iot.common.service.LoadBalance;
import com.hailin.iot.common.service.impl.ConsistentHashLoadBalancer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 用户登录的模块
 * @author zhanghailin
 *
 */
@RestController
@RequestMapping("/iot-msg")
public class UserController {



    private LoadBalance loadBalance = new ConsistentHashLoadBalancer();


}
