package com.hailin.iot.broker.cache;

import com.hailin.iot.broker.user.model.User;
import com.hailin.iot.common.contanst.LogicBit;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCache {

    private long id;

    private String userName;

    private String password;

    // 存储ip地址
    private String ip;

    //  存储的是端口
    private int port;

    // bit 位
    private long logic;

    // 最后一次 推送消息ID
    private volatile long lastMessageId;

    public static UserCache newUserCache(User user){
        UserCache userCache = UserCache.builder().id(user.getId()).password(user.getPassword()).lastMessageId(user.getLastMessageId()).logic(LogicBit.IS_ONLINE.getBit()).userName(user.getUsername()).build();
        return userCache;
    }

}
