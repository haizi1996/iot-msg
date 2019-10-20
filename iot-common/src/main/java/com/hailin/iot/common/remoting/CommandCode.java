package com.hailin.iot.common.remoting;

/**
 * 命令码
 * @author hailin
 */
public interface CommandCode {

    short HEARTBEAT_VALUE = 0;

    short value();
}
