package com.hailin.iot.common.remoting.command;

import com.hailin.iot.common.remoting.config.switches.Switch;

import java.util.BitSet;

/**
 * 协议开关
 * @author zhanghailin
 */
public class ProtocolSwitch implements Switch {

    // switch index
    public static final int CRC_SWITCH_INDEX = 0x000;

    // default value
    public static final boolean CRC_SWITCH_DEFAULT_VALUE = true;

    /** protocol switches */
    private BitSet bs = new BitSet();

    @Override
    public void turnOn(int index) {
        this.bs.set(index);
    }

    @Override
    public void turnOff(int index) {
        this.bs.clear(index);
    }

    @Override
    public boolean isOn(int index) {
        return this.bs.get(index);
    }
}
