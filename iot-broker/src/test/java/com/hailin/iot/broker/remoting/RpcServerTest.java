package com.hailin.iot.broker.remoting;

import org.junit.Test;

import static org.junit.Assert.*;

public class RpcServerTest {

    @Test
    public void  test() throws InterruptedException {
        RpcServer server = new RpcServer(8081);
        server.startup();
        server.getChannelFuture().channel().closeFuture().sync();

    }

}