package com.hailin.iot.remoting.processor;

import com.hailin.iot.remoting.ConnectionEventProcessor;
import com.hailin.iot.remoting.connection.Connection;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import lombok.extern.slf4j.Slf4j;

/**
 * 发送mqtt连接请求
 * 连接事件的监听处理
 * @author hailin
 */
@Slf4j
public class ConnectEventProcessor implements ConnectionEventProcessor {

    @Override
    public void onEvent(String remoteAddress, Connection connection) {

        log.debug(" send mqtt connect message!");
        MqttConnectMessage mqttConnectMessage = MqttMessageBuilders.connect()
                .cleanSession(true)
                .hasUser(true)
                .hasPassword(true).willQoS(MqttQoS.AT_LEAST_ONCE)
                .willTopic("")
                .keepAlive(5).protocolVersion(MqttVersion.MQTT_3_1_1)
                .clientId(connection.getChannel().id().asShortText())
                .build();
        connection.getChannel().writeAndFlush(mqttConnectMessage);
        log.debug(" send mqtt connect message end !");
    }
}
