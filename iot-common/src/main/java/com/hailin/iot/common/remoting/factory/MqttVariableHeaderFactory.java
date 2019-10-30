package com.hailin.iot.common.remoting.factory;

import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * mqtt 可变消息头的工厂
 * @author zhanghailin
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MqttVariableHeaderFactory {


    /**
     * 创建连接Ack消息的可变消息头
     */
    public static MqttConnAckVariableHeader buildMqttConnAckVariableHeader(MqttConnectReturnCode connectReturnCode, boolean sessionPresent){
        return new MqttConnAckVariableHeader(connectReturnCode, sessionPresent);
    }
}
