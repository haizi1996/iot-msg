package com.hailin.iot.broker.service;

import com.hailin.iot.common.model.Message;

/**
 * 聊天通知消息
 * @author hailin
 */
public interface NotifyChatService {

    /**
     * 发送聊消息
     */
    boolean sendPrivateChat(String accepterUser , Message message);
}
