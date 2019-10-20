package com.hailin.iot.common.remoting.protocol;

import com.hailin.iot.common.remoting.HeartbeatTrigger;
import com.hailin.iot.common.remoting.codec.Codec;
import com.hailin.iot.common.remoting.command.CommandFactory;
import com.sun.corba.se.impl.activation.CommandHandler;

/**
 * 通讯协议
 * @author hailin
 */
public interface Protocol {

    Codec getCodec();

    ProtocolCode getProtocolCode();

    HeartbeatTrigger getHeartbeatTrigger();

    CommandHandler getCommandHandler();

    CommandFactory getCommandFactory();

}
