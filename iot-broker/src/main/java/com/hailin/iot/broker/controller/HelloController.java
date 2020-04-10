package com.hailin.iot.broker.controller;

import com.hailin.iot.common.rpc.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;


public class HelloController {

    @Autowired
    private ApplicationContext applicationContext;

//    @Autowired
//    private ChatService chatService;

    public String sayHello(){
//        ChatService chatService = (ChatService)applicationContext.getBean("rpcChatService");
//        chatService.noticePrivateChat("username" , "password");
        return "哈哈哈";
    }

}
