package com.hailin.iot.broker.remoting.processor.user;

import com.hailin.iot.broker.cache.UserCache;
import com.hailin.iot.broker.cache.UserCacheInstance;
import com.hailin.iot.broker.config.ConfigValue;
import com.hailin.iot.broker.user.dao.UserMapper;
import com.hailin.iot.broker.user.model.User;
import com.hailin.iot.broker.user.model.UserExample;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.util.BrokerUtil;
import com.hailin.iot.common.util.IpUtils;
import com.hailin.iot.remoting.AsyncContext;
import com.hailin.iot.remoting.BizContext;
import com.hailin.iot.remoting.ConnectionEventType;
import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.processor.AbstractUserProcessor;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.CharsetUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ProtocolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hailin.iot.common.contanst.Contants.USER_ONLINE;

@Service
public class MqttConnectUserProcessor extends AbstractUserProcessor<MqttConnectMessage> {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProtocolConfig protocolConfig ;

    @Override
    public MqttMessageType interest() {
        return MqttMessageType.CONNECT;
    }


    @Override
    public Object handleRequest(BizContext bizContext, MqttConnectMessage mqttConnectMessage) throws Exception {
        MqttConnectPayload payload = mqttConnectMessage.payload();

        // ack 消息的可变消息头部
        MqttConnectReturnCode connectReturnCode = MqttConnectReturnCode.CONNECTION_ACCEPTED;


        //验证客户端身份
        if (StringUtils.isEmpty(payload.clientIdentifier())
                || payload.clientIdentifier().length() > 23){
            connectReturnCode = MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED;
        }

        //验证协议版本
        MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(mqttConnectMessage.variableHeader().name(),
                (byte) mqttConnectMessage.variableHeader().version());
        if (!MqttVersion.MQTT_3_1_1.equals(mqttVersion)){
            connectReturnCode = MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION;
        }
        // 清除session
        if (mqttConnectMessage.variableHeader().isCleanSession()){
            bizContext.getRemotingCtx().getChannelContext().channel().attr(Connection.MESSAGE_ID).set(new AtomicInteger(0));
        }

//        IdleStateHandler idle = bizContext.getRemotingCtx().getChannelContext().channel().pipeline().remove(IdleStateHandler.class);
//        if (idle == null){
//            return "";
//        }

        // 验证身份
        String userName = payload.userName();
        String password = new String(payload.passwordInBytes(), CharsetUtil.UTF_8);
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(userName).andPasswordEqualTo(password);
        List<User> users= userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(users)){
            connectReturnCode = MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED;
        }
        if(connectReturnCode == MqttConnectReturnCode.CONNECTION_ACCEPTED ){
            initConnection( bizContext.getRemotingCtx(), payload , users.get(0) , mqttConnectMessage.variableHeader());
        }
        //发送ConnectAck 报文
        MqttConnAckVariableHeader ackVariableHeader = new MqttConnAckVariableHeader(connectReturnCode , true);
        MqttConnAckMessage ackMessage = new MqttConnAckMessage(new MqttFixedHeader(MqttMessageType.CONNACK , false , MqttQoS.EXACTLY_ONCE , false , 0) , ackVariableHeader);
        bizContext.getRemotingCtx().writeAndFlush(ackMessage);

        return null;
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx) {


    }

    /**
     * 初花连接
     * @param ctx
     * @param user
     */
    private void initConnection(RemotingContext ctx, MqttConnectPayload payload, User user, MqttConnectVariableHeader connectVariableHeader) {
        //获取keepalive时间 单位是秒
//        int keepAlive = connectVariableHeader.keepAliveTimeSeconds();
//        ctx.getChannelContext().channel().pipeline().addBefore(Contants.IDLE_HANDLER , Contants.IDLE_STATE_HANDLER , new IdleStateHandler(0 , 0 , keepAlive , TimeUnit.SECONDS));
        Connection connection = new Connection(ctx.getChannelContext().channel());

        connection.setType(Connection.TermType.valueOf(payload.clientIdentifier()));
        connection.setUserName(payload.userName());
        connection.getChannel().attr(Connection.CONNECTION_ACK).set(Boolean.TRUE);
//        connection.getChannel().attr(Connection.CONNECTION).set(connection);
//        ctx.getChannelContext().channel().pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        ctx.getConnectionManager().add(connection , connection.getUserName());
//        connection.getConnectionManager().add(connection , payload.clientIdentifier());

        Broker broker = Broker.builder().ip(IpUtils.getLocalIpAddress()).port(protocolConfig.getPort()).score(System.currentTimeMillis()).build();
        UserCache userCache = UserCache.newUserCache(user);
        UserCacheInstance.put(userCache);
        redisTemplate.opsForHash().put(USER_ONLINE.getBytes() , payload.userName().getBytes() , BrokerUtil.serializeToByteArray(broker));
    }
}
