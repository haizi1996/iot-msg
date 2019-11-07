package com.hailin.iot.remoting.processor.impl;

import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.processor.AbstractRemotingProcessor;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttConnAckProcessor extends AbstractRemotingProcessor<MqttConnAckMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttConnAckProcessor.class);

    @Override
    public void doProcess(RemotingContext ctx, MqttConnAckMessage msg)  {
        final MqttConnAckVariableHeader header = msg.variableHeader();
        MqttConnectReturnCode connectReturnCode = header.connectReturnCode();
        switch (connectReturnCode){

            case CONNECTION_ACCEPTED :
                ctx.getChannelContext().channel().attr(Connection.CONNECTION_ACK).set(Boolean.TRUE);
                break;
            case CONNECTION_REFUSED_NOT_AUTHORIZED:
                LOGGER.error("the connection is refused not authorized");
                break;
            case CONNECTION_REFUSED_SERVER_UNAVAILABLE:
                LOGGER.error("the connection is refused server unavailable");
                break;
            case CONNECTION_REFUSED_IDENTIFIER_REJECTED:
                LOGGER.error("the connection is refused identifier rejected");
                break;
            case CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD:
                LOGGER.error("the connection is efused bad_user name or password");
                break;
            case CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION:
                LOGGER.error("the connection is refused unacceptable protocol version");
                break;
            default:
                ctx.getChannelContext().disconnect();
        }


    }
}
