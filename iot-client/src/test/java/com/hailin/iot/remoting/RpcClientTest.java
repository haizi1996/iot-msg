package com.hailin.iot.remoting;


import com.hailin.iot.remoting.connection.Connection;
import org.junit.Test;

public class RpcClientTest {

    @Test
    public void testClient() throws InterruptedException {
        RpcClient client = new RpcClient();
        client.startup();
        Connection connection = client.createStandaloneConnection("localhost:8081" , 88);
        connection.getChannel().closeFuture().sync();
    }
}