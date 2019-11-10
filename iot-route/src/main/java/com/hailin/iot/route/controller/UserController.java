package com.hailin.iot.route.controller;

import com.hailin.iot.common.model.User;
import com.hailin.iot.route.dto.BrokerInfo;
import com.hailin.iot.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

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

    @PostMapping("/user/login")
    public BrokerInfo login(@NotEmpty  String userName){
        User user = userService.findByUserName(userName);
        if(Objects.nonNull(user)){

        }
        return null;
    }

    @PostMapping("/user/register")
    public BrokerInfo registerUser(User user){
        Long num = userService.saveUser(user);
        if(num > 0){

        }
        return null;
    }
}
