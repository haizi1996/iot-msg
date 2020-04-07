package com.hailin.iot.common.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hailin.iot.common.model.User;
import com.hailin.iot.common.protoc.UserBuf;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserUtil.class);

    public static byte[] serializeToByteArray(User user){
        if(user == null){
            return null;
        }
        UserBuf.User.Builder builder = UserBuf.User.newBuilder();
        return  builder.setId(user.getId()).setUserName(user.getUserName()).setIp(user.getIp()).setPort(user.getPort())
                .setLogic(user.getLogic()).setLastMessageId(user.getLastMessageId()).build().toByteArray();
    }

    public static User deSerializationToObj(byte[] bytes){
        if(ArrayUtils.isEmpty(bytes)){
            return null;
        }
        try {
            UserBuf.User user = UserBuf.User.parseFrom(bytes);
            User res = User.builder().id(user.getId()).userName(user.getUserName()).port(user.getPort())
                    .ip(user.getIp()).logic(user.getLogic()).lastMessageId(user.getLastMessageId()).build();
            return res;
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


}
