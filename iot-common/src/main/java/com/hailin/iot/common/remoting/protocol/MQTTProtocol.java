package com.hailin.iot.common.remoting.protocol;

import com.hailin.iot.common.remoting.HeartbeatTrigger;
import com.hailin.iot.common.remoting.codec.Codec;
import com.hailin.iot.common.remoting.codec.impl.MqttCoder;
import com.hailin.iot.common.remoting.command.CommandFactory;
import com.sun.corba.se.impl.activation.CommandHandler;

/**
 * mqtt协议
 * @author hailin
 */
public class MQTTProtocol implements Protocol {

    public static final byte PROTOCOL_CODE       = (byte) 1;
    @Override
    public Codec getCodec() {
        return new MqttCoder();
    }
    @Override
    public HeartbeatTrigger getHeartbeatTrigger() {
        return null;
    }

    @Override
    public CommandHandler getCommandHandler() {
        return null;
    }

    @Override
    public CommandFactory getCommandFactory() {
        return null;
    }

    @Override
    public ProtocolCode getProtocolCode() {
        return ProtocolCode.fromBytes(PROTOCOL_CODE);
    }
}
