package com.hailin.iot.remoting;


import com.hailin.iot.remoting.connection.Connection;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import org.junit.Test;

public class RpcClientTest {

    @Test
    public void testClient() throws InterruptedException {

        RpcClient client = new RpcClient();
        client.startup();
        Connection connection = client.createStandaloneConnection("localhost:8081" , 88);
        MqttMessage connectMessage = MqttMessageBuilders.connect()
                .protocolVersion(MqttVersion.MQTT_3_1)
                .willQoS(MqttQoS.AT_LEAST_ONCE)
                .keepAlive(100)

                .build();


        connection.getChannel().writeAndFlush(connectMessage);
        connection.getChannel().closeFuture().sync();
    }
}