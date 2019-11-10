package com.hailin.iot.broker.service.provider;

import com.hailin.iot.common.rpc.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

/**
 * 聊天消息调用接口
 * @author hailin
 */
@Slf4j
@Service(version = "1.0.0")
public class ChatServiceImpl implements ChatService {

    @Override
    public boolean noticePrivateChat(String username , String messageId) {
        return false;
    }

    @Override
    public boolean noticeGroupChat() {
        return false;
    }
}
