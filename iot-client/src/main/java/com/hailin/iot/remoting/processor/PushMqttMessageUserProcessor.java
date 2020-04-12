package com.hailin.iot.remoting.processor;

import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.util.MessageUtil;
import com.hailin.iot.remoting.BizContext;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushMqttMessageUserProcessor extends AbstractUserProcessor<MqttPublishMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushMqttMessageUserProcessor.class);


    @Override
    public MqttMessageType interest() {
        return MqttMessageType.PUBLISH;
    }

    @Override
    public Object handleRequest(BizContext bizContext, MqttPublishMessage publishMessage) throws Exception {
        byte[] data ;
        if(publishMessage.payload().hasArray()){
            data = publishMessage.payload().array();
        }else {
            data = new byte[publishMessage.payload().readableBytes()];
            publishMessage.payload().readBytes(data);
        }
        Message chatMessage = MessageUtil.deSerializationToObj(data);
        LOGGER.info("接受人: " + chatMessage.getAcceptUser() + ", 收到发送人: " + chatMessage.getSendUser() + "的一条消息, 消息内容是: " + chatMessage.getContent());
        return null;
    }
}
