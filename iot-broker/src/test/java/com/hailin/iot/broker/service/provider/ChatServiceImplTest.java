package com.hailin.iot.broker.service.provider;

import com.hailin.iot.broker.config.ConsumerConfiguration;
import com.hailin.iot.common.rpc.ChatService;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ChatServiceImplTest {

    @Resource
    private ApplicationContext applicationContext;

    @Reference
    private ChatService chatService;
    @Test
    public void noticePrivateChat(){
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
//        context.start();
//        final ChatService chatService = (ChatService) context.getBean("chatService");
        chatService.noticePrivateChat("zhl" , "jjjjjjj");
    }
}