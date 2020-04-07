package com.hailin.iot.broker.user.service;

import com.hailin.iot.common.model.UserCache;

public interface UserService {

    /**
     * 保存用户
     * @param userCache 用户包装类
     */
    long saveUser(UserCache userCache);

    /**
     * 根据用户名查询用户信息
     * @param userName 用户名
     */
    UserCache findByUserName(String userName);
}
