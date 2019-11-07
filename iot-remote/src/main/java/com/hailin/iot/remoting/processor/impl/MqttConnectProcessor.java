package com.hailin.iot.remoting.processor.impl;

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
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hailin.iot.common.contanst.Contants.IDLE_HANDLER;
import static com.hailin.iot.common.contanst.Contants.IDLE_STATE_HANDLER;
import static io.netty.handler.codec.mqtt.MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION;
import static io.netty.handler.codec.mqtt.MqttVersion.MQTT_3_1_1;

/**
 * mqtt 连接建立的消息处理器
 * @author hailin
 */
public class MqttConnectProcessor extends AbstractRemotingProcessor<MqttConnectMessage> {

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

        String userName = payload.userName();
        String password = new String(payload.passwordInBytes(), CharsetUtil.UTF_8);


        //获取keepalive时间 单位是秒
        int keepAlive = connectVariableHeader.keepAliveTimeSeconds();

        IdleStateHandler idle = ctx.getChannelContext().channel().pipeline().remove(IdleStateHandler.class);
        if (idle == null){
            return;
        }

        if(connectReturnCode == MqttConnectReturnCode.CONNECTION_ACCEPTED ){
            ctx.getChannelContext().channel().pipeline().addBefore(Contants.IDLE_HANDLER , Contants.IDLE_STATE_HANDLER , new IdleStateHandler(0 , 0 , keepAlive , TimeUnit.SECONDS));
            ctx.getConnection().getChannel().attr(Connection.CONNECTION_ACK).set(Boolean.TRUE);
        }
        //发送ConnectAck 报文
        MqttConnAckVariableHeader ackVariableHeader = new MqttConnAckVariableHeader(connectReturnCode , true);
        MqttConnAckMessage ackMessage = new MqttConnAckMessage(new MqttFixedHeader(MqttMessageType.CONNACK , false , MqttQoS.EXACTLY_ONCE , false , 0) , ackVariableHeader);
        ctx.writeAndFlush(ackMessage);
    }
}