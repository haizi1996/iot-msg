package com.hailin.iot.common.dto;

import com.hailin.iot.common.model.Message;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 聊天消息的封装类
 * @author hailin
 */
@Data
@RequiredArgsConstructor
public class ChatMessage {

    private final List<Message> messages;

}
