package com.hailin.iot.route.dto;

import lombok.Getter;
import lombok.Setter;

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
}
