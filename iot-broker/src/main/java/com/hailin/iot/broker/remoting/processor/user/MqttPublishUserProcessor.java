package com.hailin.iot.broker.remoting.processor.user;

import com.hailin.iot.common.dto.ChatMessage;
import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.util.ChatMessageUtil;
import com.hailin.iot.leaf.IDGen;
import com.hailin.iot.leaf.snowflake.SnowflakeIDGenImpl;
import com.hailin.iot.remoting.AsyncContext;
import com.hailin.iot.remoting.BizContext;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.processor.AbstractUserProcessor;
import com.hailin.iot.remoting.processor.DefaultExecutorSelector;
import com.hailin.iot.store.service.StoreService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//@Service
public class MqttPublishUserProcessor extends AbstractUserProcessor<MqttPublishMessage> {

    @Value("${idgen.redisUrl}")
    private String redisUrl;

    @Value("${idgen.port")
    private Integer port;

    @Autowired
    private StoreService storeService;

    private IDGen idGen = new SnowflakeIDGenImpl(redisUrl , port );


    public MqttPublishUserProcessor() {
        this.executorSelector = new DefaultExecutorSelector("MqttPublishUserProcessor");
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, MqttPublishMessage request) {

    }

    @Override
    public Object handleRequest(BizContext bizContext, MqttMessage request) throws Exception {
        MqttPublishMessage publishMessage = (MqttPublishMessage)request;
        Connection connection = bizContext.getConnection();
        ChatMessage chatMessage = ChatMessageUtil.deSerializationToObj(publishMessage.payload().array());
        Message message = Message.builder()
                .sendTime(System.currentTimeMillis())
                .messageId(idGen.get().getId())
                .sendUser(connection.getUserName())
                .content(chatMessage.getContent())
                .acceptUser(chatMessage.getAcceptUser())
                .messageBit(chatMessage.getMessageBit()).build();
        storeService.storePrivateChatMessage(message);
        return null;
    }
}
