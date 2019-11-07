package com.hailin.iot.broker.remoting;

import com.hailin.iot.remoting.ConnectionManager;
import com.hailin.iot.remoting.RemotingAddressParser;
import com.hailin.iot.remoting.RpcRemoting;
public class RpcServerRemoting extends RpcRemoting {



    public RpcServerRemoting( RemotingAddressParser addressParser, ConnectionManager connectionManager) {
        super( addressParser, connectionManager);
    }
}
