package com.hailin.iot.broker.netty;

import com.hailin.iot.common.util.IpUtils;
import io.netty.bootstrap.ServerBootstrap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * netty的启动器
 * @author zhanghailin
 */
@Component
public class NettyBoot {


    @Value("${netty.port}")
    private List<String> port;

    public void startBootStrap() {
        String ip = IpUtils.getLocalIpAddress();
        ServerBootstrap bootstrap = new ServerBootstrap();
//        bootstrap.group()

    }

}
