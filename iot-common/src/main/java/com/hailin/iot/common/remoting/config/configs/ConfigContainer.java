package com.hailin.iot.common.remoting.config.configs;

import com.hailin.iot.common.remoting.config.ConfigType;

/**
 * 配置容器的接口
 * @author hailin
 */
public interface ConfigContainer  {

    boolean contains(ConfigType configType, ConfigItem configItem);


    <T> T get(ConfigType configType, ConfigItem configItem);


    void set(ConfigType configType, ConfigItem configItem, Object value);
}