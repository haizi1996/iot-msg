package com.hailin.iot.broker.user.model;

/**
 * 存储DB的对象
 * @author hailin
 */
public class User {

    private long id;

    private String userName;

    private String password;

    // 最后一次 推送消息ID
    private long lastMessageId;
}
