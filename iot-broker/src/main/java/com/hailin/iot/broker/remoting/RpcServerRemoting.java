package com.hailin.iot.broker.remoting;

import com.hailin.iot.common.remoting.ConnectionManager;
import com.hailin.iot.common.remoting.RemotingAddressParser;
import com.hailin.iot.common.remoting.RpcRemoting;
import com.hailin.iot.common.remoting.command.CommandFactory;

public class RpcServerRemoting extends RpcRemoting {

    public RpcServerRemoting(CommandFactory commandFactory) {
        super(commandFactory);
    }

    public RpcServerRemoting(CommandFactory commandFactory, RemotingAddressParser addressParser, ConnectionManager connectionManager) {
        super(commandFactory, addressParser, connectionManager);
    }
}
