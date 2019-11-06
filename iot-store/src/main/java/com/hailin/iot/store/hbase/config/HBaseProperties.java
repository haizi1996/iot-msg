package com.hailin.iot.store.hbase.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "spring.data.hbase")
@Setter
@Getter
public class HBaseProperties {

    private Map<String , String> config;
}
