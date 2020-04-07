package com.hailin.iot.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * broker的信息
 * @author zhanghailin
 */
@Getter
@Setter
public class BrokerInfo {

    private String hostname;

    private Integer port;

    private String token;

    public String getUrl() {
        return hostname + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrokerInfo that = (BrokerInfo) o;
        return hostname.equals(that.hostname) &&
                port.equals(that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname, port);
    }
}
