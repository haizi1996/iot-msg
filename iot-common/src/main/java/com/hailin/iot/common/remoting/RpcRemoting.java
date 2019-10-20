package com.hailin.iot.common.remoting;

import com.hailin.iot.common.remoting.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RpcRemoting extends BaseRemoting {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRemoting.class);

    protected RemotingAddressParser addressParser;

    protected ConnectionManager connectionManager;

    public RpcRemoting(CommandFactory commandFactory) {
        super(commandFactory);
    }

    public RpcRemoting(CommandFactory commandFactory, RemotingAddressParser addressParser, ConnectionManager connectionManager) {
        super(commandFactory);
        this.addressParser = addressParser;
        this.connectionManager = connectionManager;
    }
}
