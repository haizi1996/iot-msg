package com.hailin.iot.remoting.processor;

import com.hailin.iot.remoting.UserProcessor;
import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class UserProcessorRegisterHelper {


    public static void registerUserProcessor(UserProcessor<?> processor,
                                             ConcurrentHashMap<MqttMessageType, UserProcessor<?>> userProcessors) {
        if (null == processor) {
            throw new RuntimeException("User processor should not be null!");
        }
        if (processor instanceof MultiInterestUserProcessor) {
            registerUserProcessor(((MultiInterestUserProcessor) processor), userProcessors);
        } else {
            if (Objects.nonNull(processor.interest())) {
                throw new RuntimeException("Processor interest should not be blank!");
            }
            UserProcessor<?> preProcessor = userProcessors.putIfAbsent(processor.interest(),
                processor);
            if (preProcessor != null) {
                String errMsg = "Processor with interest key ["
                                + processor.interest()
                                + "] has already been registered to rpc server, can not register again!";
                throw new RuntimeException(errMsg);
            }
        }
    }

    private static void registerUserProcessor(MultiInterestUserProcessor<?> processor,
                                              ConcurrentHashMap<MqttMessageType, UserProcessor<?>> userProcessors) {
        if (null == processor.multiInterest() || processor.multiInterest().isEmpty()) {
            throw new RuntimeException("Processor interest should not be blank!");
        }
        for (MqttMessageType interest : processor.multiInterest()) {
            UserProcessor<?> preProcessor = userProcessors.putIfAbsent(interest, processor);
            if (preProcessor != null) {
                String errMsg = "Processor with interest key ["
                                + interest
                                + "] has already been registered to rpc server, can not register again!";
                throw new RuntimeException(errMsg);
            }
        }

    }
}
