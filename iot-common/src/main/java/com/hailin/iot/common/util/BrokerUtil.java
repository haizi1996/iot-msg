package com.hailin.iot.common.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hailin.iot.common.Broker;
import com.hailin.iot.common.protoc.BrokerBuf;
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
 * broker的工具类
 * 主要是一些序列化和反序列化
 * @author hailin
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrokerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerUtil.class);

    public static byte[] serializeToByteArray(Broker broker){
        if(broker == null){
            return null;
        }
        BrokerBuf.Broker.Builder builder = BrokerBuf.Broker.newBuilder();
        return  builder.setHost(broker.getHost())
        .setPort(broker.getPort()).build().toByteArray();
    }


    public static Broker deSerializationToObj(byte[] bytes){
        if(ArrayUtils.isEmpty(bytes)){
            return null;
        }
        try {
            BrokerBuf.Broker broker = BrokerBuf.Broker.parseFrom(bytes);
            Broker res = Broker.builder().host(broker.getHost()).port(broker.getPort()).build();
            return res;
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static List<Broker> deSerializationToObj(Collection<byte[]> bytes){
        if(CollectionUtils.isEmpty(bytes)){
            return null;
        }
        return bytes.stream().map(BrokerUtil::deSerializationToObj).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
