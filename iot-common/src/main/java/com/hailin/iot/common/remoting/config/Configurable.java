package com.hailin.iot.common.remoting.config;

/**
 * 配置接口
 * @author hailin
 */
public interface Configurable {

    /**
     * Get the option value.
     *
     * @param option target option
     * @return BoltOption
     */
    <T> T option(BoltOption<T> option);

    /**
     * 设置option这个参数的value
     */
    <T> Configurable option(BoltOption<T> option, T value);
}
