package com.hailin.iot.broker.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hailin.iot.broker.cache.UserCache;
import com.hailin.iot.common.protoc.UserBuf;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserUtil.class);

    public static byte[] serializeToByteArray(UserCache userCache){
        if(userCache == null){
            return null;
        }
        UserBuf.User.Builder builder = UserBuf.User.newBuilder();
        return  builder.setId(userCache.getId()).setUserName(userCache.getUserName()).setIp(userCache.getIp()).setPort(userCache.getPort())
                .setLogic(userCache.getLogic()).setLastMessageId(userCache.getLastMessageId()).build().toByteArray();
    }

    public static UserCache deSerializationToObj(byte[] bytes){
        if(ArrayUtils.isEmpty(bytes)){
            return null;
        }
        try {
            UserBuf.User user = UserBuf.User.parseFrom(bytes);
            UserCache res = UserCache.builder().id(user.getId()).userName(user.getUserName()).port(user.getPort())
                    .ip(user.getIp()).logic(user.getLogic()).lastMessageId(user.getLastMessageId()).build();
            return res;
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


}
