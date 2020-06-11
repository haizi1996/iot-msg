package com.hailin.iot.broker.config;

import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
public class ConfigValue {

    @Value("${netty.ports}")
    private String ports;

    public List<Integer> getPorts() {
        return  Splitter.on(",").omitEmptyStrings().splitToList(ports).stream().map(Integer::parseInt).collect(Collectors.toList());
    }
}
