package com.hailin.iot.remoting.config;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Options的容器
 * @author hailin
 */
public class IotOptions {

    private ConcurrentHashMap<IotOption<?>, Object> options = new ConcurrentHashMap<IotOption<?>, Object>();


    @SuppressWarnings("unchecked")
    public <T> T option(IotOption<T> option) {
        Object value = options.get(option);
        if (value == null) {
            value = option.getDefaultValue();
        }

        return value == null ? null : (T) value;
    }


    public <T> IotOptions option(IotOption<T> option, T value) {
        if (value == null) {
            options.remove(option);
            return this;
        }

        options.put(option, value);
        return this;
    }
}
