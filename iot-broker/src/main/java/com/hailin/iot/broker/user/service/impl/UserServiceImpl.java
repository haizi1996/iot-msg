package com.hailin.iot.broker.user.service.impl;

import com.hailin.iot.broker.user.dao.UserMapper;
import com.hailin.iot.broker.user.model.User;
import com.hailin.iot.broker.user.model.UserExample;
import com.hailin.iot.broker.user.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public long saveUser(User userCache) {
        return userMapper.insert(userCache);
    }

    @Override
    public User findByUserName(String userName) {
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(userName);
        List<User> res = userMapper.selectByExample(example);
        return CollectionUtils.isEmpty(res) ? null : res.get(0);
    }
}
