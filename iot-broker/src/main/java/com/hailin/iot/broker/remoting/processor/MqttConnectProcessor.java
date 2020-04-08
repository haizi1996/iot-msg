package com.hailin.iot.broker.remoting.processor;

import com.hailin.iot.broker.config.ConfigValue;
import com.hailin.iot.broker.user.dao.UserMapper;
import com.hailin.iot.broker.user.model.User;
import com.hailin.iot.broker.user.model.UserExample;
import com.hailin.iot.common.model.Broker;
import com.hailin.iot.common.util.BrokerUtil;
import com.hailin.iot.common.util.IpUtils;
import com.hailin.iot.common.contanst.Contants;
import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hailin.iot.common.contanst.Contants.USER_ONLINE;

/**
 * mqtt 连接建立的消息处理器
 * @author hailin
 */
@Service
public class MqttConnectProcessor extends AbstractRemotingProcessor<MqttConnectMessage> {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ConfigValue configValue;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void doProcess(RemotingContext ctx, MqttConnectMessage msg) throws Exception {
        //连接建立的可变消息头
        MqttConnectVariableHeader connectVariableHeader = msg.variableHeader();

        MqttConnectPayload payload = msg.payload();
        // ack 消息的可变消息头部
        MqttConnectReturnCode connectReturnCode = MqttConnectReturnCode.CONNECTION_ACCEPTED;
        //验证客户端身份
        if (StringUtils.isEmpty(payload.clientIdentifier())
                || payload.clientIdentifier().length() > 23){
            connectReturnCode = MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED;
        }

        //验证协议版本
        MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(connectVariableHeader.name(),
                (byte) connectVariableHeader.version());
        if (MqttVersion.MQTT_3_1_1.equals(mqttVersion)){
            connectReturnCode = MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION;
        }
        // 清除session
        if (connectVariableHeader.isCleanSession()){
            ctx.getChannelContext().channel().attr(Connection.MESSAGE_ID).set(new AtomicInteger(0));
        }
        // 验证身份
        String userName = payload.userName();
        String password = new String(payload.passwordInBytes(), CharsetUtil.UTF_8);
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(userName).andPasswordEqualTo(password);
        List<User> users= userMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(users)){
            connectReturnCode = MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED;
        }

        IdleStateHandler idle = ctx.getChannelContext().channel().pipeline().remove(IdleStateHandler.class);
        if (idle == null){
            return;
        }

        if(connectReturnCode == MqttConnectReturnCode.CONNECTION_ACCEPTED ){
            initConnection(ctx , payload , connectVariableHeader);
        }
        //发送ConnectAck 报文
        MqttConnAckVariableHeader ackVariableHeader = new MqttConnAckVariableHeader(connectReturnCode , true);
        MqttConnAckMessage ackMessage = new MqttConnAckMessage(new MqttFixedHeader(MqttMessageType.CONNACK , false , MqttQoS.EXACTLY_ONCE , false , 0) , ackVariableHeader);
        ctx.writeAndFlush(ackMessage);
    }

    /**
     * 初花连接
     * @param ctx
     */
    private void initConnection(RemotingContext ctx , MqttConnectPayload payload , MqttConnectVariableHeader connectVariableHeader) {
        //获取keepalive时间 单位是秒
        int keepAlive = connectVariableHeader.keepAliveTimeSeconds();
        ctx.getChannelContext().channel().pipeline().addBefore(Contants.IDLE_HANDLER , Contants.IDLE_STATE_HANDLER , new IdleStateHandler(0 , 0 , keepAlive , TimeUnit.SECONDS));
        Connection connection = ctx.getConnection();
        connection.setType(Connection.TermType.valueOf(payload.clientIdentifier()));
        connection.setUserName(payload.userName());
        connection.getChannel().attr(Connection.CONNECTION_ACK).set(Boolean.TRUE);
        connection.getConnectionManager().add(connection , payload.clientIdentifier());
        Broker broker = Broker.builder().host(IpUtils.getLocalIpAddress()).port(configValue.getPort()).score(System.currentTimeMillis()).build();
        redisTemplate.opsForHash().put(USER_ONLINE.getBytes() , payload.userName().getBytes() , BrokerUtil.serializeToByteArray(broker));
    }


}