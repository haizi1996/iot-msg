package com.hailin.iot.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 聊天消息的实体
 * @author zhanghailin
 */
@Getter
@Setter
@Builder
public class Message {


    //消息ID
    private long messageId;

    //发送方
    private String sendUser;

    //接收方
    private String acceptUser;

    //消息类型 群聊,私聊,朋友圈
    //消息内容的类型  文本还是视频，图片
    //文本类型的消息 可能包含图片之类的
    private int messageBit;

    //发送时间
    private long sendTime;

    //消息内容
    private String content;

}
