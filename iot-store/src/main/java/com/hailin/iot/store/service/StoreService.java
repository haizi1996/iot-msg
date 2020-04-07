package com.hailin.iot.store.service;

import com.hailin.iot.common.contanst.MessageBit;
import com.hailin.iot.common.model.Message;

import java.util.List;

/**
 * 封装对外提供的接口
 * @author zhanghailin
 */
public interface StoreService {
    //列族
    String familyName = "message";

    String SESSION_COLUMN = "session_Id";
    String SEND_USER_COLUMN = "send_user";
    String ACCEPT_USER_COLUMN = "accept_User";
    String CONTENT_COLUMN = "content";


    /**
     * 存儲群聊消息
     * @param message
     */
    void storeGroupChatMessage(Message message);
    /**
     * 存儲私聊消息
     * @param message
     */
    void storePrivateChatMessage(Message message);


    default void storeMessage(Message message){
        if (MessageBit.PRIVATE_CHAT.isBit( message.getMessageBit())){
            storePrivateChatMessage(message);
        }else if (MessageBit.GROUP_CHAT.isBit(message.getMessageBit())){
            storeGroupChatMessage(message);
        }
    }


    List<Message> getMessageByRowKey(byte[] startRowKey , boolean inclusive , Integer limit);
}
