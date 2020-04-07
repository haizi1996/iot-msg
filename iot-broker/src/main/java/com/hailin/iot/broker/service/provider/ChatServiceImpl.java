package com.hailin.iot.broker.service.provider;

import com.hailin.iot.broker.cache.UserCacheInstance;
import com.hailin.iot.broker.remoting.RpcServer;
import com.hailin.iot.broker.service.NotifyChatService;
import com.hailin.iot.common.contanst.Contants;
import com.hailin.iot.common.contanst.LogicBit;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.model.User;
import com.hailin.iot.common.rpc.ChatService;
import com.hailin.iot.common.service.LoadBalance;
import com.hailin.iot.common.service.impl.ConsistentHashLoadBalancer;
import com.hailin.iot.common.util.IpUtils;
import com.hailin.iot.common.util.UserUtil;
import com.hailin.iot.remoting.NamedThreadFactory;
import com.hailin.iot.store.hbase.HbaseUtils;
import com.hailin.iot.store.service.StoreService;
import com.hailin.iot.store.timeline.TimeLine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.hailin.iot.common.contanst.Contants.REDIS_USER_KEY;

/**
 * 聊天消息调用接口
 * @author hailin
 */
@Slf4j
@Service("chatService")
public class ChatServiceImpl implements ChatService {

    private Executor executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() , new NamedThreadFactory("ChatServiceImpl"));

    private static final int timeout = 500;

    @Autowired
    private RpcServer rpcServer;

    @Autowired
    private RedisTemplate<String, ?> redisTemplate;


    @Autowired
    private StoreService storeService;

    private static final int PUSH_MESSAGE_SIZE = 10;

    @Resource
    private NotifyChatService notifyChatService;

    @Override
    public boolean noticePrivateChat(String acceptUsername , Long messageId ) {
        log.debug(acceptUsername + " invoke id " + messageId);
        User user = UserCacheInstance.get(acceptUsername);
        //  如果当前用户的信息没有在本broker上 可能是一致性hash重定向错了 可能是不在线
        if(Objects.isNull(user)){
            byte[] data = redisTemplate.< String , byte[] >opsForHash().get(REDIS_USER_KEY , user.getUserName());
            if (ArrayUtils.isEmpty(data)){
                return true;
            }else {
                User cacheUser = UserUtil.deSerializationToObj(data);
                // 如果在线的话
                if(LogicBit.IS_ONLINE.isBit(cacheUser.getLogic())){
                    String ip = IpUtils.longToIP(cacheUser.getIp());
                    Broker broker = Broker.builder().host(ip).port(cacheUser.getPort()).score(System.currentTimeMillis()).build();
                    RpcContext.getContext().set(Contants.BROKER , broker);
                    notifyChatService.sendPrivateChat(acceptUsername , )
                }
            }
        }

        // 如果已经推送过消息了 或者这个接收用户在推送消息
        if((user.getLastMessageId() != 0 && messageId < user.getLastMessageId()) || LogicBit.IS_PUSH.isBit(user.getLogic())){
            return true;
        }
        executor.execute(() -> sendPrivateChatMessages(acceptUsername , messageId ));
        return true;
    }

    /**
     * 发送私聊信息
     */
    private void sendPrivateChatMessages(String acceptUsername, Long messageId ) {
        List<Message> messages = storeService.getMessageByRowKey(HbaseUtils.buildRowKeyAsc(acceptUsername , messageId) , false , PUSH_MESSAGE_SIZE);
        if(CollectionUtils.isEmpty(messages)){
            UserCacheInstance.get(acceptUsername).setLogic(LogicBit.IS_PUSH.addBit(UserCacheInstance.get(acceptUsername).getLogic()));
            // todo  检查连接
            return ;
        }
        try {
            rpcServer.sendMessageToUser(acceptUsername ,messages , timeout);
            UserCacheInstance.get(acceptUsername).setLastMessageId(messages.get(messages.size() - 1).getMessageId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.execute(() -> sendPrivateChatMessages(acceptUsername , UserCacheInstance.get(acceptUsername).getLastMessageId()));
    }


    @Override
    public boolean noticeGroupChat() {
        return false;
    }
}
