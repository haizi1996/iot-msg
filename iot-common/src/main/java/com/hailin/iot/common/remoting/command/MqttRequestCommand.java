package com.hailin.iot.common.remoting.command;

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

}
