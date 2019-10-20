package com.hailin.iot.common.remoting.command;

import com.hailin.iot.common.exception.DeserializationException;
import com.hailin.iot.common.exception.SerializationException;
import com.hailin.iot.common.remoting.CommandCode;
import com.hailin.iot.common.remoting.InvokeContext;

import java.io.Serializable;

/**
 * 命令接口
 * @author hailin
 */
public interface RemotingCommand extends Serializable {

    CommandCode getCmdCode();

    int getId();

    InvokeContext getInvokeContext();

    byte getSerializer();

    void serialize() throws SerializationException;

    void deserialize() throws DeserializationException;

    void serializeContent(InvokeContext invokeContext) throws SerializationException;

    void deserializeContent(InvokeContext invokeContext) throws DeserializationException;
}
