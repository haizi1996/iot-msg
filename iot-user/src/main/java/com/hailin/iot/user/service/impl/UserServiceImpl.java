package com.hailin.iot.user.service.impl;

import com.hailin.iot.common.model.User;
import com.hailin.iot.user.dao.UserMapper;
import com.hailin.iot.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public long saveUser(User user) {
        return userMapper.saveUser(user);
    }

    @Override
    public User findByUserName(String userName) {
        return userMapper.findByUserName(userName);
    }
}
