package com.hailin.iot.broker.util;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ip地址的工具类
 * @author zhanghailin
 */
@NoArgsConstructor
public class IpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IpUtils.class);

    /**
     * 获取本机IP地址
     */
    public static String getLocalIpAddress(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.error(e.getMessage() , e);
            throw new RuntimeException(e);
        }
    }


}
