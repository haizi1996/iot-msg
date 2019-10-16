package com.hailin.iot.broker.remoting;

import com.hailin.iot.common.remoting.AbstractRemotingServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcServer extends AbstractRemotingServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private ServerBootstrap bootstrap ;

    private ChannelFuture channelFuture ;

    private ConnectionEv
}
