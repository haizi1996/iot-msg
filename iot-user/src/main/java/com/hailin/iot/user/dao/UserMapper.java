package com.hailin.iot.user.dao;


import com.hailin.iot.common.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    long saveUser(User user);
    /**
     * 根据用户名查询用户信息
     * @param userName 用户名
     */
    User findByUserName(@Param("userName") String userName);
}
