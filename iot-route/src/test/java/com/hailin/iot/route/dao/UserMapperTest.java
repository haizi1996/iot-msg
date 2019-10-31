package com.hailin.iot.route.dao;

import com.hailin.iot.route.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {

    @Resource
    private UserMapper userMapper;

    @Test
    public void saveUser() {
        User user = new User();
        user.setUserName("zhangsan");
        user.setPassword("123456");
        long id = userMapper.saveUser(user);
        System.out.println(id);
        System.out.println(user.getId());
    }

    @Test
    public void findByUserName() {
        String userName = "zhangsan";

        User user = userMapper.findByUserName(userName);
        System.out.println(user);

    }
}