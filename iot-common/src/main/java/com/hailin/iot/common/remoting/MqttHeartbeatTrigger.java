package com.hailin.iot.common.remoting;

import com.hailin.iot.common.remoting.command.CommandFactory;
import com.hailin.iot.common.remoting.command.ResponseCommand;
import com.hailin.iot.common.remoting.config.ConfigManager;
import com.hailin.iot.common.remoting.connection.Connection;
import com.hailin.iot.common.remoting.future.DefaultInvokeFuture;
import com.hailin.iot.common.remoting.future.InvokeCallbackListener;
import com.hailin.iot.common.remoting.future.InvokeFuture;
import com.hailin.iot.common.util.RemotingUtil;
import com.hailin.iot.common.util.TimerHolder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * RPC 心跳触发
 * @author zhanghailin
 */
public class MqttHeartbeatTrigger implements HeartbeatTrigger {

    private static final Logger logger= LoggerFactory.getLogger(MqttHeartbeatTrigger.class);

    public static final Integer maxCount = ConfigManager.tcp_idle_maxtimes();

    private static final long heartbeatTimeoutMillis = 1000;



    public MqttHeartbeatTrigger() {
    }

    @Override
    public void heartbeatTriggered(ChannelHandlerContext ctx) throws Exception {
        Integer heartBeatTimes = ctx.channel().attr(Connection.HEARTBEAT_COUNT).get();
        final Connection conn = ctx.channel().attr(Connection.CONNECTION).get();
        if(heartBeatTimes >= maxCount){
            try {
                conn.close();
                logger.error("Heartbeat failed for {} times, close the connection from client side: {} ",
                        heartBeatTimes, RemotingUtil.parseRemoteAddress(ctx.channel()));
            }catch (Exception e){
                logger.warn("Exception caught when closing connection in SharableHandler.", e);
            }
        }else {
            Boolean heartbeatSwitch = ctx.channel().attr(Connection.HEARTBEAT_SWITCH).get();
            if (! heartbeatSwitch){
                return;
            }
            MqttMessage heartMessage = MqttMessageFactory.newMessage(new MqttFixedHeader(MqttMessageType.PINGREQ , false , MqttQoS.AT_LEAST_ONCE , false ,0) , null , null);

            final InvokeFuture future = new DefaultInvokeFuture(0, new InvokeCallbackListener() {
                @Override
                public void onResponse(InvokeFuture future) {
                    ResponseCommand response;
                    try {
                        response = future.waitResponse(0);
                    } catch (InterruptedException e) {
                        logger.error("Heartbeat ack process error!  from remoteAddr={}",
                                RemotingUtil.parseRemoteAddress(ctx.channel()),
                                e);
                        return;
                    }
                    if (response != null
                            && response.getResponseStatus() == ResponseStatus.SUCCESS) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Heartbeat ack received!  from remoteAddr={}",
                                    RemotingUtil.parseRemoteAddress(ctx.channel()));
                        }
                        ctx.channel().attr(Connection.HEARTBEAT_COUNT).set(0);
                    } else {
                        if (response != null
                                && response.getResponseStatus() == ResponseStatus.TIMEOUT) {
                            logger.error("Heartbeat timeout! The address is {}",
                                    RemotingUtil.parseRemoteAddress(ctx.channel()));
                        } else {
                            logger.error(
                                    "Heartbeat exception caught! Error code={}, The address is {}",
                                    response == null ? null : response.getResponseStatus(),
                                    RemotingUtil.parseRemoteAddress(ctx.channel()));
                        }
                        Integer times = ctx.channel().attr(Connection.HEARTBEAT_COUNT).get();
                        ctx.channel().attr(Connection.HEARTBEAT_COUNT).set(times + 1);
                    }
                }

                @Override
                public String getRemoteAddress() {
                    return ctx.channel().remoteAddress().toString();
                }
            } , null);
            ctx.writeAndFlush(heartMessage).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Send heartbeat done!  to remoteAddr={}"
                                    , RemotingUtil.parseRemoteAddress(ctx.channel()));
                        }
                    } else {
                        logger.error("Send heartbeat failed!  to remoteAddr={}",
                                RemotingUtil.parseRemoteAddress(ctx.channel()));
                    }
                }
            });

            TimerHolder.getTimer().newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    InvokeFuture future = conn.getHeartbeatFuture();
                    if (future != null) {
                        future.putResponse(null);
                        future.tryAsyncExecuteInvokeCallbackAbnormally();
                    }
                }
            }, heartbeatTimeoutMillis, TimeUnit.MILLISECONDS);
        }

    }
}
