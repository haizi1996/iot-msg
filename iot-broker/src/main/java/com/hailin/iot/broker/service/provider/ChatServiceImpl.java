package com.hailin.iot.broker.service.provider;

import com.hailin.iot.broker.remoting.RpcServer;
import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.rpc.ChatService;
import com.hailin.iot.common.util.MessageUtil;
import com.hailin.iot.remoting.InvokeContext;
import com.hailin.iot.remoting.NamedThreadFactory;
import com.hailin.iot.store.service.StoreService;
import com.hailin.iot.store.timeline.TimeLine;
import com.hailin.iot.store.timeline.impl.RedisTimeLine;
import com.hailin.iot.store.timeline.model.TimeLineModel;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 聊天消息调用接口
 * @author hailin
 */
@Slf4j
@Service(version = "1.0.0")
public class ChatServiceImpl implements ChatService {

    private Executor executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() , new NamedThreadFactory("ChatServiceImpl"));

    private static final int timeout = 500;

    @Autowired
    private RpcServer rpcServer;

    @Autowired
    private TimeLine redisTimeLine;

    @Autowired
    private StoreService storeService;

    @Override
    public boolean noticePrivateChat(String username , String messageId) {

        List<TimeLineModel> timeLineModels = redisTimeLine.getModels(username.getBytes() , 2);
        if(CollectionUtils.isEmpty(timeLineModels)){
            return true;
        }
        List<Message> messages = storeService.getMessageByRowKeys(timeLineModels.get(0).getKey() , timeLineModels.size());
        executor.execute(sendPrivateChatMessages(username , messages));
        return true;
    }

    /**
     * 发送私聊信息
     */
    private Runnable sendPrivateChatMessages(String username, List<Message> messages) {
        return ()-> messages.forEach(message ->
        {
            try {
                rpcServer.invokeWithCallback(rpcServer.getConnectionManager().get(username).getUrl() ,message, new InvokeContext() , null , timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }




    @Override
    public boolean noticeGroupChat() {
        return false;
    }
}
