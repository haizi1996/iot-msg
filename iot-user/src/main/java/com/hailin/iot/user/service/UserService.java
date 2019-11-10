package com.hailin.iot.user.service;

import com.hailin.iot.common.model.User;

public interface UserService {

    /**
     * 保存用户
     * @param user 用户包装类
     */
    long saveUser(User user);

    /**
     * 根据用户名查询用户信息
     * @param userName 用户名
     */
    User findByUserName(String userName);
}
