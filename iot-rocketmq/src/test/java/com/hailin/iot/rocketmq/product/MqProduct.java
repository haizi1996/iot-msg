package com.hailin.iot.rocketmq.product;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MqProduct {

    @Autowired
    private DefaultMQProducer producer;

    @Test
    public void testProduct() throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message msg = new Message("TopicTest" /* Topic */,
                "TagA" /* Tag */, ("Hello RocketMQ").getBytes(RemotingHelper.DEFAULT_CHARSET)); /* Message body */
        SendResult sendResult = producer.send(msg);

        System.out.println(sendResult.getMsgId());
        producer.shutdown();
    }
}
