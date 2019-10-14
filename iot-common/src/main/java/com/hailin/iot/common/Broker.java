package com.hailin.iot.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    private String host;
    //端口
    private int port;

}
