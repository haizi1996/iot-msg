package com.hailin.iot.common.remoting.command;

import com.hailin.iot.common.exception.DeserializationException;
import com.hailin.iot.common.exception.SerializationException;
import com.hailin.iot.common.remoting.CommandCode;
import com.hailin.iot.common.remoting.InvokeContext;
import com.hailin.iot.common.remoting.config.ConfigManager;
import com.hailin.iot.common.remoting.protocol.MQTTProtocol;
import com.hailin.iot.common.remoting.protocol.ProtocolCode;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public abstract class MqttCommand implements RemotingCommand{

    private static final long serialVersionUID = -3570261012462596503L;

    //命令码
    private CommandCode cmdCode;

    private byte version = 0x1;

    private byte type ;

    private byte serializer = ConfigManager.serializer;

    private ProtocolSwitch protocolSwitch = new ProtocolSwitch();

    private int id;

    private MqttMessage message;

    private InvokeContext invokeContext;

    public MqttCommand() {
    }

    public MqttCommand(byte type) {
        this();
        this.type = type;
    }

    public MqttCommand(CommandCode cmdCode) {
        this();
        this.cmdCode = cmdCode;
    }

    public MqttCommand(CommandCode cmdCode, byte type) {
        this(cmdCode);
        this.type = type;
    }

    public MqttCommand(CommandCode cmdCode, byte version, byte type) {
        this(cmdCode , type);
        this.version = version;
    }

    @Override
    public ProtocolCode getProtocolCode() {
        return ProtocolCode.fromBytes(MQTTProtocol.PROTOCOL_CODE);
    }

    @Override
    public ProtocolSwitch getProtocolSwitch() {
        return protocolSwitch;
    }

    @Override
    public void serialize() throws SerializationException {

    }

    @Override
    public void deserialize() throws DeserializationException {

    }

    @Override
    public void serializeContent(InvokeContext invokeContext) throws SerializationException {

    }

    @Override
    public void deserializeContent(InvokeContext invokeContext) throws DeserializationException {

    }
}
