package com.hailin.iot.remoting.processor.impl;


import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import org.springframework.stereotype.Service;


/**
 * mqtt 连接建立的消息处理器
 * @author hailin
 */
@Service
public class MqttConnectProcessor extends AbstractRemotingProcessor<MqttConnectMessage> {



    @Override
    public void preProcessRemotingContext(RemotingContext ctx, MqttConnectMessage msg , long timestamp) throws Exception {

    }




}
