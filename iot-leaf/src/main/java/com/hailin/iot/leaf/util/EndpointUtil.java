package com.hailin.iot.leaf.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.protoc.BrokerBuf;
import com.hailin.iot.leaf.common.Endpoint;
import com.hailin.iot.leaf.common.protoc.EndpointBuf;
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
public class EndpointUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointUtil.class);

    public static byte[] serializeToByteArray(Endpoint endpoint){
        if(endpoint == null){
            return null;
        }
        EndpointBuf.Endpoint.Builder builder = EndpointBuf.Endpoint.newBuilder();
        return  builder.setIp(endpoint.getIp())
        .setPort(endpoint.getPort()).setTimestamp(endpoint.getTimestamp())
                .build().toByteArray();
    }


    public static Endpoint deSerializationToObj(byte[] bytes){
        if(ArrayUtils.isEmpty(bytes)){
            return null;
        }
        try {
            EndpointBuf.Endpoint endpoint = EndpointBuf.Endpoint.parseFrom(bytes);
            Endpoint res = Endpoint.builder().ip(endpoint.getIp()).port(endpoint.getPort()).timestamp(endpoint.getTimestamp()).build();
            return res;
        } catch (InvalidProtocolBufferException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static List<Endpoint> deSerializationToObj(Collection<byte[]> bytes){
        if(CollectionUtils.isEmpty(bytes)){
            return null;
        }
        return bytes.stream().map(EndpointUtil::deSerializationToObj).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
