package com.hailin.iot.broker.service.impl;

import com.hailin.iot.broker.remoting.RpcServer;
import com.hailin.iot.broker.service.NotifyChatService;
import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.rpc.ChatService;
import com.hailin.iot.common.util.MessageUtil;
import com.hailin.iot.remoting.connection.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NotifyChatServiceImpl implements NotifyChatService {

    @Autowired
    private RpcServer rpcServer;

    @Autowired
    private ChatService chatService;

    @Override
    public boolean sendPrivateChat(String accepterUser, Message message) {
        //获取
        Connection connection = rpcServer.getConnectionManager().get(accepterUser);
        if(connection != null){
            connection.getChannel().writeAndFlush(buildMqttPublishMessage(message));
            return true;
        }
        chatService.noticePrivateChat(accepterUser , message.getMessageId());
        return true;
    }

    private MqttMessage buildMqttPublishMessage(Message message) {
        ByteBufAllocator byteBufAllocator = ByteBufAllocator.DEFAULT;
        ByteBuf byteBuf = byteBufAllocator.buffer();
        byteBuf.writeBytes(MessageUtil.serializeToByteArray(message));

        return MqttMessageBuilders.publish().topicName("" )
                .qos(MqttQoS.AT_LEAST_ONCE)
                .retained(false).messageId(Long.valueOf(message.getMessageId()).intValue())
                .payload(byteBuf).build();

    }
}
