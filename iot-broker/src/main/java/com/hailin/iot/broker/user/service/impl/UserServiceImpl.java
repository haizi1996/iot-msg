package com.hailin.iot.broker.user.service.impl;

import com.hailin.iot.broker.user.dao.UserMapper;
import com.hailin.iot.broker.user.service.UserService;
import com.hailin.iot.common.model.UserCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public long saveUser(UserCache userCache) {
        return userMapper.saveUser(userCache);
    }

    @Override
    public UserCache findByUserName(String userName) {
        return userMapper.findByUserName(userName);
    }
}
