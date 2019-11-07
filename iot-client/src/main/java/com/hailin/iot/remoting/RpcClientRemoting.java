package com.hailin.iot.remoting;

public class RpcClientRemoting extends RpcRemoting {
    public RpcClientRemoting( RemotingAddressParser addressParser,
                             DefaultConnectionManager connectionManager) {
        super( addressParser, connectionManager);
    }
}
