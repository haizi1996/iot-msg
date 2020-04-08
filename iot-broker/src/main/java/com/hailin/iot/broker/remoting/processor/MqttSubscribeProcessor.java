package com.hailin.iot.broker.remoting.processor;

import com.hailin.iot.broker.remoting.SubscribePool;
import com.hailin.iot.remoting.RemotingAddressParser;
import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.Url;
import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import com.hailin.iot.remoting.util.RemotingUtil;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;

import java.util.List;
import java.util.stream.Collectors;

import static com.hailin.iot.remoting.connection.Connection.ADDRESS_PARSER;
import static com.hailin.iot.remoting.connection.Connection.CLIENT_IDENTIFIER;

/**
 * mqtt消息订阅处理器
 * @author hailin
 */
public class MqttSubscribeProcessor extends AbstractRemotingProcessor<MqttSubscribeMessage> {

    @Override
    public void preProcessRemotingContext(RemotingContext ctx, MqttSubscribeMessage msg , long timestamp) throws Exception {
        MqttFixedHeader fixedHeader = msg.fixedHeader();
        List<MqttTopicSubscription> topicSubscriptions = msg.payload().topicSubscriptions();

        //第一次请求
        if(!fixedHeader.isDup()){
            //获取channel的用户名，以及ip地址
            registTopicSubscription(topicSubscriptions , ctx);
            MqttSubAckMessage message = buildSubackMessage(topicSubscriptions , msg);
            ctx.writeAndFlush(message);
            //构建一个消息同步任务  将用户订阅的主题 之历史消息 push给用户
            Runnable runnable = null;
            getExecutor().execute(runnable);

        }
    }

    /**
     * 构建一个subAck消息
     * @param topicSubscriptions 订阅的主题
     * @param msg 订阅信息
     */
    private MqttSubAckMessage buildSubackMessage(List<MqttTopicSubscription> topicSubscriptions, MqttSubscribeMessage msg) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK , false , msg.fixedHeader().qosLevel() , false , 0);
        List<Integer> mqttQoS = topicSubscriptions.stream().map(MqttTopicSubscription::qualityOfService).map(MqttQoS::value).collect(Collectors.toList());
        return new MqttSubAckMessage(mqttFixedHeader , msg.variableHeader() , new MqttSubAckPayload(mqttQoS));
    }

    /**
     * 注册订阅主题
     * @param topicSubscriptions 订阅的主题
     */
    private void registTopicSubscription(List<MqttTopicSubscription> topicSubscriptions, RemotingContext ctx) {

        String token = ctx.getChannelContext().channel().attr(CLIENT_IDENTIFIER).get();
        RemotingAddressParser addressParser = ctx.getChannelContext().channel().attr(ADDRESS_PARSER).get();
        Url url = addressParser.parse(RemotingUtil.parseRemoteAddress(ctx.getChannelContext().channel()));
        //连接池
        String poolKey = url.getUniqueKey();
        //注册订阅主题到订阅池
        for (MqttTopicSubscription topic : topicSubscriptions) {
            String topicName = topic.topicName();
            SubscribePool.getInstance().register(topicName , poolKey);
        }
    }
}
