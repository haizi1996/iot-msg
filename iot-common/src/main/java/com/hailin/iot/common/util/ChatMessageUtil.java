package com.hailin.iot.common.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hailin.iot.common.dto.ChatMessage;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.protoc.BrokerBuf;
import com.hailin.iot.common.protoc.ChatMessageBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * chatMessage的工具类
 * 主要是一些序列化和反序列化
 * @author hailin
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageUtil.class);

    public static byte[] serializeToByteArray(ChatMessage chatMessage){
        if(chatMessage == null){
            return null;
        }
        ChatMessageBuf.ChatMessage.Builder builder = ChatMessageBuf.ChatMessage.newBuilder();
        return  builder.setAcceptUser(chatMessage.getAcceptUser())
        .setContent(chatMessage.getContent()).setMessageBit(chatMessage.getMessageBit()).build().toByteArray();
    }


    public static ChatMessage deSerializationToObj(byte[] bytes){
        if(ArrayUtils.isEmpty(bytes)){
            return null;
        }
        try {
            ChatMessageBuf.ChatMessage chatMessage = ChatMessageBuf.ChatMessage.parseFrom(bytes);
            ChatMessage res = ChatMessage.builder().acceptUser(chatMessage.getAcceptUser()).messageBit(chatMessage.getMessageBit())
                    .content(chatMessage.getContent()).build();
            return res;
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static List<ChatMessage> deSerializationToObj(Collection<byte[]> bytes){
        if(CollectionUtils.isEmpty(bytes)){
            return null;
        }
        return bytes.stream().map(ChatMessageUtil::deSerializationToObj).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
