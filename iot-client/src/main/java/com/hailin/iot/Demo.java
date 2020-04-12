package com.hailin.iot;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.hailin.iot.common.contanst.MessageBit;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.util.MessageUtil;
import com.hailin.iot.remoting.RpcClient;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.processor.PushMqttMessageUserProcessor;
import com.hailin.iot.remoting.util.IDGenerator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Demo {


    public static void main(String[] args) throws InterruptedException {
        RpcClient rpcClient = new RpcClient();
        rpcClient.registerUserProcessor(new PushMqttMessageUserProcessor());
        rpcClient.startup();
        // 连上broker1
        // 张三登录
//        Connection connection = rpcClient.createStandaloneConnection(IpUtils.getLocalIpAddress() , 5001 , 5000 * 60);
//        System.out.println(connection.toString());
//        MqttConnectMessage zhansanLogin = MqttMessageBuilders.connect().willQoS(MqttQoS.AT_LEAST_ONCE).cleanSession(true).protocolVersion(MqttVersion.MQTT_3_1_1).keepAlive(60).willFlag(false).willRetain(false).willTopic("test").hasPassword(true).password("123456".getBytes())
//                .hasUser(true).username("zhangsan").clientId("APP").build();
//
//        connection.getChannel().writeAndFlush(zhansanLogin);
//        System.out.println(connection.toString());
        // 连上broker2
        // 王五登录
        int threads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        List<Connection> connections = Lists.newArrayList();
        int i = 0;
        while (i++ <= 2){
            connections.add(getConnection("user"+i , rpcClient));
        }
        // 王五给张三发消息
        i = 0;
        while(i ++ < threads){
            executorService.execute(() -> {
                int chats = 100000 , init = 0;
                while(init < chats){
                    Connection connection = connections.get(new Random().nextInt(2) + 1);
                    sendMessage(connection , connections.get(new Random().nextInt(2) + 1).getUserName() );
                    init ++;
                }
            });
        }

        new CountDownLatch(1).await();

    }
    public static   void sendMessage(  Connection connection , String acceptUser ){
        String content = connection.getUserName() + " ===> " + acceptUser + " ; 消息内容 : " + UUID.randomUUID();
        Message chatMessage = Message.builder().acceptUser(acceptUser).content(content)
                .messageId(4).sendUser(connection.getUserName()).messageBit(MessageBit.PRIVATE_CHAT.getBit()).sendTime(System.currentTimeMillis()).build();
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes(MessageUtil.serializeToByteArray(chatMessage));
        MqttMessage message = MqttMessageBuilders.publish().topicName("private").messageId(IDGenerator.nextId()).qos(MqttQoS.AT_LEAST_ONCE).retained(true)
                .payload(byteBuf).build();
        connection.getChannel().writeAndFlush(message);
    }

    public static   Connection getConnection(String userName ,RpcClient rpcClient ){
//        Broker broker = getBroker(userName);
        Broker broker = Broker.builder().ip("47.114.109.210").port(5000).build() ;//getBroker(userName);
        Connection connection = rpcClient.createStandaloneConnection(broker.getIp() , broker.getPort() , 5000 * 60);
        MqttConnectMessage wangwuConnect = MqttMessageBuilders.connect().willQoS(MqttQoS.AT_LEAST_ONCE).cleanSession(true).protocolVersion(MqttVersion.MQTT_3_1_1).keepAlive(60).willFlag(false).willRetain(false).willTopic("test").hasPassword(true).password("123456".getBytes())
                .hasUser(true).username(userName).clientId("APP").build();
        connection.getChannel().writeAndFlush(wangwuConnect);
        connection.setUserName(userName);
        while (true){
            if(connection.getChannel().attr(Connection.CONNECTION_ACK) != null && Objects.nonNull( connection.getChannel().attr(Connection.CONNECTION_ACK).get()) &&connection.getChannel().attr(Connection.CONNECTION_ACK).get() ){
                return connection;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static Broker getBroker(String username) {
        String url = "http://47.111.180.163:8080/iot-route/" + username;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpResponse response = null;
            HttpGet get = new HttpGet(url);
            response = httpclient.execute(get);
            String respCtn = EntityUtils.toString(response.getEntity());
            return JSON.parseObject(respCtn, Broker.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                }
            }
        }


    }

}
