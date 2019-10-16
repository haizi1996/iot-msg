package com.hailin.iot.common.remoting.config.switches;

/**
 * 开关接口
 * 采用位运算来表示开关打开
 * @author hailin
 */
public interface Switch {

    /**
     * 打开所索引index处的开关
     */
    void turnOn(int index);

    /**
     * 关闭所索引index处的开关
     */
    void turnOff(int index);

    /**
     * 检查index处的开关是否打开
     */
    boolean isOn(int index);
}


