package com.hailin.iot.remoting;


import com.hailin.iot.common.remoting.connection.Connection;

public class RpcClientTest {
    public static void main(String[] args) throws InterruptedException {
        RpcClient client = new RpcClient();
        client.startup();
        Connection connection = client.createStandaloneConnection("localhost:8081" , 88);

        connection.getChannel().closeFuture().sync();
    }
}