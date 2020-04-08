package com.hailin.iot.broker.remoting.processor.user;

import com.hailin.iot.broker.service.NotifyChatService;
import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.util.MessageUtil;
import com.hailin.iot.leaf.IDGen;
import com.hailin.iot.leaf.snowflake.SnowflakeIDGenImpl;
import com.hailin.iot.remoting.BizContext;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.processor.AbstractUserProcessor;
import com.hailin.iot.remoting.processor.DefaultExecutorSelector;
import com.hailin.iot.store.service.StoreService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Service
public class MqttPublishUserProcessor extends AbstractUserProcessor<MqttPublishMessage> {

    @Value("${idgen.redisUrl}")
    private String redisUrl;

    @Value("${idgen.port}")
    private Integer port;

    @Autowired
    private StoreService storeService;

    private IDGen idGen ;

    @Autowired
    private NotifyChatService notifyChatService;


    @Override
    public MqttMessageType interest() {
        return MqttMessageType.PUBLISH;
    }

    @PostConstruct
    public void init(){
        idGen = new SnowflakeIDGenImpl(redisUrl , port );
        this.executorSelector = new DefaultExecutorSelector("MqttPublishUserProcessor");
    }


    @Override
    public Object handleRequest(BizContext bizContext, MqttMessage request) throws Exception {
        MqttPublishMessage publishMessage = (MqttPublishMessage)request;
        Connection connection = bizContext.getConnection();

        byte[] data ;
        if(publishMessage.payload().hasArray()){
            data = publishMessage.payload().array();
        }else {
            data = new byte[publishMessage.payload().readableBytes()];
            publishMessage.payload().readBytes(data);
        }

        Message chatMessage = MessageUtil.deSerializationToObj(data);
        Message message = Message.builder()
                .sendTime(System.currentTimeMillis())
                .messageId(idGen.get().getId())
                .sendUser(chatMessage.getSendUser())
                .content(chatMessage.getContent())
                .acceptUser(chatMessage.getAcceptUser())
                .messageBit(chatMessage.getMessageBit()).build();
        storeService.storeMessage(message);
        // 多终端同步 需要发送到其它信息 会保证多个终端在同一个broker上
        sendMultiTermMessage(bizContext.getConnection() , message);

        // 通知 消息接收者
        notifyChatService.sendMessageChat(message.getAcceptUser() , message);
        return null;
    }

    private void sendMultiTermMessage(Connection connection, Message message) {
        if (Objects.isNull(connection.getPool())){
            return;
        }
        for ( Connection item : connection.getPool().getAll()) {
            if (Objects.equals( item , connection)){
                continue;
            }
            connection.getChannel().writeAndFlush(message);
        }
    }
}
