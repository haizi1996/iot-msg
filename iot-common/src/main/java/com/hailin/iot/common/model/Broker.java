package com.hailin.iot.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
public class Broker implements Comparable<Broker> {
    //ip地址
    private String ip;
    //端口
    private int port;

    private long score;

    public Broker() {
    }


    public Broker setScore(long score) {
        this.score = score;
        return this;
    }

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

    public String getUrl() {
        return ip+":"+port;
    }

    @Override
    public int compareTo(Broker o) {
        if (equals(o)){
            return 0;
        }
        if(o.score != score){
            return Long.compare(score, o.score);
        }
        if (ip.compareTo(o.ip) != 0){
            return ip.compareTo(o.ip);
        }
        return Integer.compare(port , o.port);
    }
}
