package com.hailin.iot.remoting.processor;

import com.hailin.iot.remoting.UserProcessor;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.List;


public interface MultiInterestUserProcessor<T extends MqttMessage> extends UserProcessor<T> {

    List<MqttMessageType> multiInterest();

}
