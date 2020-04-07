package com.hailin.iot.broker.service;

import com.hailin.iot.common.contanst.MessageBit;
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

    /**
     * 发送聊天消息
     * @param accepterUser 接收人
     * @param message 接收信息
     * @return
     */
    default boolean sendMessageChat(String accepterUser , Message message){
        if (MessageBit.PRIVATE_CHAT.isBit(message.getMessageBit())){
            return sendPrivateChat(accepterUser, message);
        }
        return true;
    }
}
