package com.hailin.iot.broker.controller;

import com.hailin.iot.common.rpc.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {

    @Autowired
    private ApplicationContext applicationContext;

//    @Autowired
//    private ChatService chatService;

    @GetMapping("/hello")
    public String sayHello(){
//        ChatService chatService = (ChatService)applicationContext.getBean("rpcChatService");
//        chatService.noticePrivateChat("username" , "password");
        return "哈哈哈";
    }

}
