package com.hailin.iot.broker.remoting.processor;

import com.hailin.iot.broker.remoting.SubscribePool;
import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribePayload;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttUnsubScribeProcessor extends AbstractRemotingProcessor<MqttUnsubscribeMessage> {

    @Override
    public void doProcess(RemotingContext ctx, MqttUnsubscribeMessage msg) throws Exception {
        MqttFixedHeader fixedHeader = msg.fixedHeader();
        MqttUnsubscribePayload payload = msg.payload();
        if(!fixedHeader.isDup()){
            payload.topics().stream().peek(topic -> SubscribePool.getInstance().unSubscribe(topic));
        }
        MqttMessage mqttMessage = buildUnsubAckMessage(msg);
        ctx.writeAndFlush(mqttMessage);

    }
    private MqttMessage buildUnsubAckMessage(MqttUnsubscribeMessage msg) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK , msg.fixedHeader().isDup() , msg.fixedHeader().qosLevel() , false,0 );
        MqttUnsubscribePayload payload = new MqttUnsubscribePayload(msg.payload().topics());
        return new MqttUnsubscribeMessage(fixedHeader , msg.variableHeader() , payload);
    }
}
