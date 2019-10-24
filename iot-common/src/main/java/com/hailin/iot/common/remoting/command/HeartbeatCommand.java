package com.hailin.iot.common.remoting.command;

import com.hailin.iot.common.remoting.CommonCommandCode;
import com.hailin.iot.common.util.IDGenerator;

public class HeartbeatCommand extends MqttRequestCommand {


    public HeartbeatCommand() {
        super(CommonCommandCode.HEARTBEAT);
        this.setId(IDGenerator.nextId());
    }

}
