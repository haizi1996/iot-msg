package com.hailin.iot.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * broker信息
 * @author hailin
 */
@Getter
@Setter
@Builder
@ToString
public class Broker {
    //ip地址
    private String ip;
    //端口
    private int port;

    private long score;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Broker broker = (Broker) o;
        return port == broker.port &&
                Objects.equals(ip, broker.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
