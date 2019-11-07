package com.hailin.iot.remoting.config;

/**
 * 配置接口
 * @author hailin
 */
public interface Configurable {

    /**
     * Get the option value.
     *
     * @param option target option
     * @return IotOption
     */
    <T> T option(IotOption<T> option);

    /**
     * 设置option这个参数的value
     */
    <T> Configurable option(IotOption<T> option, T value);
}
