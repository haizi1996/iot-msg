package com.hailin.iot.remoting.util;

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
                .setMessageBit(message.getMessageBit())
                .setContent(message.getContent()).build().toByteArray();
    }


    public static Message deSerializationToObj(byte[] bytes){
        if(ArrayUtils.isEmpty(bytes)){
            return null;
        }
        try {
            MessageBuf.Message message = MessageBuf.Message.parseFrom(bytes);
            Message res = Message.builder().sendUser(message.getSendUser())
                    .acceptUser(message.getAcceptUser())
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
