package com.hailin.iot.common.remoting.config.configs;

import com.hailin.iot.common.remoting.config.ConfigType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的配置容器的实现类
 *
 * @author hailin
 */
public class DefaultConfigContainer implements ConfigContainer {
    /** logger */
    private static final Logger logger      = LoggerFactory.getLogger("CommonDefault");

    /**
     * use a hash map to store the user configs with different config types and config items.
     */
    private Map<ConfigType, Map<ConfigItem, Object>> userConfigs = new HashMap<ConfigType, Map<ConfigItem, Object>>();

    @Override
    public boolean contains(ConfigType configType, ConfigItem configItem) {
        validate(configType, configItem);
        return null != userConfigs.get(configType)
               && userConfigs.get(configType).containsKey(configItem);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(ConfigType configType, ConfigItem configItem) {
        validate(configType, configItem);
        if (userConfigs.containsKey(configType)) {
            return (T) userConfigs.get(configType).get(configItem);
        }
        return null;
    }

    @Override
    public void set(ConfigType configType, ConfigItem configItem, Object value) {
        validate(configType, configItem, value);
        Map<ConfigItem, Object> items = userConfigs.get(configType);
        if (null == items) {
            items = new HashMap<ConfigItem, Object>();
            userConfigs.put(configType, items);
        }
        Object prev = items.put(configItem, value);
        if (null != prev) {
            logger.warn("the value of ConfigType {}, ConfigItem {} changed from {} to {}",
                configType, configItem, prev.toString(), value.toString());
        }
    }

    private void validate(ConfigType configType, ConfigItem configItem) {
        if (null == configType || null == configItem) {
            throw new IllegalArgumentException(String.format(
                "ConfigType {%s}, ConfigItem {%s} should not be null!", configType, configItem));
        }
    }

    private void validate(ConfigType configType, ConfigItem configItem, Object value) {
        if (null == configType || null == configItem || null == value) {
            throw new IllegalArgumentException(String.format(
                "ConfigType {%s}, ConfigItem {%s}, value {%s} should not be null!", configType,
                configItem, value == null ? null : value.toString()));
        }
    }
}