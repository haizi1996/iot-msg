package com.hailin.iot.rocketmq.cofig;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rocketMq配置信息
 * @author hailin
 */
@Configuration
public class RocketMqConfig {

    @Value("${rocketMq.config.nameServer}")
    private String nameSer;

    @Value("${rocketMq.config.privateChat.topic}")
    private String privateChatTopic;

    @Value("${rocketMq.config.privateChat.product.group}")
    private String productGroup;

    @Bean
    public DefaultMQProducer buildDefaultMQProducer(){
        DefaultMQProducer producer = new
                DefaultMQProducer(productGroup);
        // Specify name server addresses.
        producer.setNamesrvAddr(nameSer);
        //Launch the instance.
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return producer;
    }

}
