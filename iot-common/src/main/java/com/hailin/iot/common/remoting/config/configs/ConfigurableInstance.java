package com.hailin.iot.common.remoting.config.configs;

import com.hailin.iot.common.remoting.config.switches.GlobalSwitch;

public interface ConfigurableInstance extends NettyConfigure{

    /**
     * 获取配置容器
     */
    ConfigContainer conf();

    /**
     * 获取全局的配置开关
     */
    GlobalSwitch switches();
}
