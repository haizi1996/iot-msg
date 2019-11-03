package com.hailin.iot.remoting;

import com.hailin.iot.common.remoting.DefaultConnectionManager;
import com.hailin.iot.common.remoting.RemotingAddressParser;
import com.hailin.iot.common.remoting.RpcRemoting;

public class RpcClientRemoting extends RpcRemoting {
    public RpcClientRemoting( RemotingAddressParser addressParser,
                             DefaultConnectionManager connectionManager) {
        super( addressParser, connectionManager);
    }
}
