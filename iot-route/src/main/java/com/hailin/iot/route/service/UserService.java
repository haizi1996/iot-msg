package com.hailin.iot.route.service;

import com.hailin.iot.route.model.User;
import org.apache.ibatis.annotations.Param;

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
    User findByUserName( String userName);
}
