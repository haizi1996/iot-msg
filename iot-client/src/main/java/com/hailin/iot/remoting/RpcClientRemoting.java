package com.hailin.iot.remoting;

import com.hailin.iot.common.remoting.DefaultConnectionManager;
import com.hailin.iot.common.remoting.RemotingAddressParser;
import com.hailin.iot.common.remoting.RpcRemoting;
import com.hailin.iot.common.remoting.command.CommandFactory;

public class RpcClientRemoting extends RpcRemoting {
    public RpcClientRemoting(CommandFactory commandFactory, RemotingAddressParser addressParser,
                             DefaultConnectionManager connectionManager) {
        super(commandFactory, addressParser, connectionManager);
    }
}
