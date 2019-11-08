package com.hailin.iot.store.service.impl;

import com.hailin.iot.common.contanst.MessageBit;
import com.hailin.iot.common.model.Message;
import com.hailin.iot.leaf.IDGen;
import com.hailin.iot.leaf.snowflake.SnowflakeIDGenImpl;
import com.hailin.iot.store.service.StoreService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StoreServiceImplTest {

    private Message message ;

    private IDGen idGen;

    @Autowired
    private StoreService storeService;

    @Before
    public void buildMessage(){
        idGen = new SnowflakeIDGenImpl("redis://127.0.0.1:6379/1" , 6379 );
        message = Message.builder().messageId(idGen.get().getId())
                .messageBit(MessageBit.PRIVATE_CHAT.getBit())
                .content("哈哈哈哈哈哈").sendUser("zhanghai").acceptUser("zhanglin").sendTime(System.currentTimeMillis())
                .build();

    }

    @Test
    public void storeGroupChatMessage() {
    }

    @Test
    public void storePrivateChatMessage() {
        storeService.storePrivateChatMessage(message);
    }
}