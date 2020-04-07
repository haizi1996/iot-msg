package com.hailin.iot.broker.user.dao;


import com.hailin.iot.common.model.UserCache;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    long saveUser(UserCache userCache);
    /**
     * 根据用户名查询用户信息
     * @param userName 用户名
     */
    UserCache findByUserName(@Param("userName") String userName);
}
