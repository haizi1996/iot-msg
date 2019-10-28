package com.hailin.iot.route.controller;

import com.hailin.iot.route.dto.BrokerInfo;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotEmpty;

/**
 * 用户登录的模块
 * @author zhanghailin
 *
 */
@Controller
@RequestMapping
public class UserController {


    @RequestMapping

    public BrokerInfo login(@NotEmpty  String userName , @NotEmpty String password){

    }
}
