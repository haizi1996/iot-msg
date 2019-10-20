package com.hailin.iot.common.remoting.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 配置参数 key value对
 * @param <T>
 */
@Setter
@Getter
public class IotOption<T> {

    private final String name;
    private T            defaultValue;

    protected IotOption(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public static <T> IotOption<T> valueOf(String name) {
        return new IotOption<T>(name, null);
    }

    public static <T> IotOption<T> valueOf(String name, T defaultValue) {
        return new IotOption<T>(name, defaultValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IotOption<?> that = (IotOption<?>) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
