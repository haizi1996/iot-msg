package com.hailin.iot.common.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 聊天消息的封装类
 * @author hailin
 */
@Data
@Builder
public class ChatMessage {

    //接收方
    private String acceptUser;

    //消息类型 群聊,私聊,朋友圈
    //消息内容的类型  文本还是视频，图片
    //文本类型的消息 可能包含图片之类的
    private int messageBit;

    // 消息内容
    private String content;

}
