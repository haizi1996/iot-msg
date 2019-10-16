package com.hailin.iot.common.remoting.config.switches;

import com.hailin.iot.common.remoting.config.ConfigManager;

import java.util.BitSet;

/**
 * 全局开关
 * @author hailin
 */
public class GlobalSwitch implements Switch {

    // switches
    public static final int CONN_RECONNECT_SWITCH           = 0;
    public static final int CONN_MONITOR_SWITCH             = 1;
    public static final int SERVER_MANAGE_CONNECTION_SWITCH = 2;
    public static final int SERVER_SYNC_STOP                = 3;

    /** user settings */
    private BitSet userSettings                    = new BitSet();

    public GlobalSwitch() {
        if (ConfigManager.conn_reconnect_switch()) {
            userSettings.set(CONN_RECONNECT_SWITCH);
        } else {
            userSettings.clear(CONN_RECONNECT_SWITCH);
        }

        if (ConfigManager.conn_monitor_switch()) {
            userSettings.set(CONN_MONITOR_SWITCH);
        } else {
            userSettings.clear(CONN_MONITOR_SWITCH);
        }
    }
    @Override
    public void turnOn(int index) {
        this.userSettings.set(index);
    }

    @Override
    public void turnOff(int index) {
        this.userSettings.clear(index);
    }

    @Override
    public boolean isOn(int index) {
        return this.userSettings.get(index);
    }
}
