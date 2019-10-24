package com.hailin.iot.common.remoting.command;

import com.hailin.iot.common.remoting.CommandCode;
import com.hailin.iot.common.remoting.mqtt.MqttCommandType;
import lombok.Getter;
import lombok.Setter;


/**
 * mqtt 请求命令
 * @author zhanghailin
 */

@Getter
@Setter
public class MqttRequestCommand extends MqttCommand {


    private int timeout = -1;

    public MqttRequestCommand() {
        super(MqttCommandType.REQUEST);
    }

    public MqttRequestCommand(CommandCode code) {
        super(code , MqttCommandType.REQUEST);
    }

    public MqttRequestCommand(byte type, CommandCode code) {
        super( code , type);
    }

    public MqttRequestCommand(byte version, byte type, CommandCode code) {
        super(code , version, type);
    }
}
