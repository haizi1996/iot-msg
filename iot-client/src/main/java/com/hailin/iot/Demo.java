package com.hailin.iot;

import com.hailin.iot.common.contanst.MessageBit;
import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.util.IpUtils;
import com.hailin.iot.common.util.MessageUtil;
import com.hailin.iot.remoting.RpcClient;
import com.hailin.iot.remoting.connection.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;

import java.util.concurrent.CountDownLatch;

public class Demo {

    public static void main(String[] args) throws InterruptedException {
        RpcClient rpcClient = new RpcClient();
        rpcClient.startup();

        // 张三登录
        Connection connection = rpcClient.createStandaloneConnection(IpUtils.getLocalIpAddress() , 5000 , 5000);
        System.out.println(connection.toString());
        MqttConnectMessage zhansanLogin = MqttMessageBuilders.connect().willQoS(MqttQoS.AT_LEAST_ONCE).cleanSession(true).protocolVersion(MqttVersion.MQTT_3_1_1).keepAlive(60).willFlag(false).willRetain(false).willTopic("test").hasPassword(true).password("123456".getBytes())
                .hasUser(true).username("zhangsan").clientId("APP").build();

        connection.getChannel().writeAndFlush(zhansanLogin);

        // 王五登录
        Connection wangwu = rpcClient.createStandaloneConnection(IpUtils.getLocalIpAddress() , 5000 , 5000);
        System.out.println(connection.toString());
        MqttConnectMessage wangwuConnect = MqttMessageBuilders.connect().willQoS(MqttQoS.AT_LEAST_ONCE).cleanSession(true).protocolVersion(MqttVersion.MQTT_3_1_1).keepAlive(60).willFlag(false).willRetain(false).willTopic("test").hasPassword(true).password("123456".getBytes())
                .hasUser(true).username("wangwu").clientId("APP").build();
        wangwu.getChannel().writeAndFlush(wangwuConnect);

        // 王五给张三发消息
        Message chatMessage = Message.builder().acceptUser("zhangsan").content("呵呵呵")
                .messageId(4).sendUser("wangwu").messageBit(MessageBit.PRIVATE_CHAT.getBit()).sendTime(System.currentTimeMillis()).build();
        ByteBuf byteBuf = Unpooled.buffer();
        MqttMessage message = MqttMessageBuilders.publish().topicName("private").messageId(4).qos(MqttQoS.AT_LEAST_ONCE).retained(false)
                .payload(byteBuf).build();
        byteBuf.writeBytes(MessageUtil.serializeToByteArray(chatMessage));
        wangwu.getChannel().writeAndFlush(message);
        new CountDownLatch(1).await();

    }
}
