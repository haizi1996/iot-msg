package com.hailin.iot.remoting;

import com.hailin.iot.remoting.config.IotOption;
import com.hailin.iot.remoting.config.IotOptions;
import com.hailin.iot.remoting.config.ConfigManager;
import com.hailin.iot.remoting.config.ConfigType;
import com.hailin.iot.remoting.config.Configurable;
import com.hailin.iot.remoting.config.configs.ConfigContainer;
import com.hailin.iot.remoting.config.configs.ConfigItem;
import com.hailin.iot.remoting.config.configs.ConfigurableInstance;
import com.hailin.iot.remoting.config.configs.DefaultConfigContainer;
import com.hailin.iot.remoting.config.switches.GlobalSwitch;

import java.util.Objects;

public abstract class AbstractIotClient extends AbstractLifeCycle implements IotClient , ConfigurableInstance {

    private final IotOptions options;

    private final ConfigType configType;

    private final GlobalSwitch globalSwitch;

    private final ConfigContainer configContainer;

    public AbstractIotClient() {
        this.options = new IotOptions();
        this.configType = ConfigType.CLIENT_SIDE;
        this.globalSwitch = new GlobalSwitch();
        this.configContainer = new DefaultConfigContainer();
    }

    @Override
    public <T> T option(IotOption<T> option){
        return options.option(option);
    }

    @Override
    public <T> Configurable option(IotOption<T> option , T value){
        options.option(option , value);
        return this;
    }

    @Override
    public ConfigContainer conf() {
        return this.configContainer;
    }

    @Override
    public GlobalSwitch switches() {
        return this.globalSwitch;
    }

    @Override
    public void initWriteBufferWaterMark(int low, int high) {
        this.configContainer.set(configType , ConfigItem.NETTY_BUFFER_LOW_WATER_MARK , low);
        this.configContainer.set(configType , ConfigItem.NETTY_BUFFER_HIGH_WATER_MARK , high);
    }

    @Override
    public int netty_buffer_low_watermark() {
        Object config = configContainer.get(configType , ConfigItem.NETTY_BUFFER_LOW_WATER_MARK);
        if (Objects.isNull(config)){
            return ConfigManager.netty_buffer_low_watermark();
        }else {
            return (Integer)config;
        }
    }

    @Override
    public int netty_buffer_high_watermark() {
        Object config = configContainer.get(configType , ConfigItem.NETTY_BUFFER_HIGH_WATER_MARK);
        if (Objects.isNull(config)){
            return ConfigManager.netty_buffer_high_watermark();
        }else {
            return (Integer)config;
        }
    }
}
