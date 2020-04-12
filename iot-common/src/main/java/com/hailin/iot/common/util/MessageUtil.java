package com.hailin.iot.common.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.protoc.MessageBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtil.class);

    public static byte[] serializeToByteArray(Message message){
        if(message == null){
            return null;
        }
        MessageBuf.Message.Builder builder = MessageBuf.Message.newBuilder();
        return  builder.setAcceptUser(message.getAcceptUser())
                .setSendUser(message.getSendUser())
                .setId(message.getMessageId())
                .setSendTime(message.getSendTime())
                .setMessageBit(message.getMessageBit())
                .setContent(message.getContent()).build().toByteArray();
    }

    public static MessageBuf.Message transforToMessageBuf(Message message){
        if(message == null){
            return null;
        }
        MessageBuf.Message.Builder builder = MessageBuf.Message.newBuilder();
        return  builder.setAcceptUser(message.getAcceptUser())
                .setSendUser(message.getSendUser())
                .setId(message.getMessageId())
                .setSendTime(message.getSendTime())
                .setMessageBit(message.getMessageBit())
                .setContent(message.getContent()).build();
    }
    public static List<MessageBuf.Message> transforToMessageBufs(List<Message> messages){
        if(CollectionUtils.isEmpty(messages)){
            return Collections.EMPTY_LIST;
        }
        return messages.stream().map(MessageUtil::transforToMessageBuf).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static Message transforToMessage(MessageBuf.Message messageBuf){
        if(messageBuf == null){
            return null;
        }
        return  Message.builder().acceptUser(messageBuf.getAcceptUser())
                .sendUser(messageBuf.getSendUser()).sendTime(messageBuf.getSendTime()).messageId(messageBuf.getId()).content(messageBuf.getContent()).messageBit(messageBuf.getMessageBit()).build();
    }
    public static List<Message> transforToMessages(List<MessageBuf.Message> messageBufs){
        if(CollectionUtils.isEmpty(messageBufs)){
            return Collections.EMPTY_LIST;
        }
        return messageBufs.stream().map(MessageUtil::transforToMessage).filter(Objects::nonNull).collect(Collectors.toList());
    }


    public static Message deSerializationToObj(byte[] bytes){
        if(ArrayUtils.isEmpty(bytes)){
            return null;
        }
        try {
            MessageBuf.Message message = MessageBuf.Message.parseFrom(bytes);
            Message res = Message.builder().sendUser(message.getSendUser())
                    .acceptUser(message.getAcceptUser())
                    .sendTime(message.getSendTime())
                    .messageId(message.getId())
                    .messageBit(message.getMessageBit())
                    .content(message.getContent()).build();
            return res;
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static List<Message> deSerializationToObj(Collection<byte[]> bytes){
        if(CollectionUtils.isEmpty(bytes)){
            return null;
        }
        return bytes.stream().map(MessageUtil::deSerializationToObj).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
