package com.hailin.iot.broker;

import com.hailin.iot.common.rpc.ChatService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@SpringBootApplication
@Service
public class ConsumerApplication {

    @Reference(version = "1.0.0")
    private ChatService chatService;

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(ConsumerApplication.class, args);
        ConsumerApplication application = context.getBean(ConsumerApplication.class);

        Boolean result = application.noticePrivateChat("world" , "jjjjj");
        System.out.println("result: " + result);
    }

    public boolean noticePrivateChat(String username , String messageId){
       return chatService.noticePrivateChat(username , messageId);
    }

}
