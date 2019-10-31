package com.hailin.iot.route.service.impl;

import com.hailin.iot.route.dao.UserMapper;
import com.hailin.iot.route.model.User;
import com.hailin.iot.route.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public long saveUser(User user) {
        return 0;
    }

    @Override
    public User findByUserName(String userName) {
        return null;
    }
}
