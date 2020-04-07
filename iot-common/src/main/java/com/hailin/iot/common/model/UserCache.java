package com.hailin.iot.common.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCache {

    private long id;

    private String userName;

    private String password;

    // 存储ip地址
    private long ip;

    //  存储的是端口
    private int port;

    // bit 位
    private long logic;

    // 最后一次 推送消息ID
    private volatile long lastMessageId;



}
