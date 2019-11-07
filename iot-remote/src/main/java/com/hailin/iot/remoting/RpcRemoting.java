package com.hailin.iot.remoting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RpcRemoting extends BaseRemoting {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcRemoting.class);

    protected RemotingAddressParser addressParser;

    protected ConnectionManager connectionManager;


    public RpcRemoting( RemotingAddressParser addressParser, ConnectionManager connectionManager) {
        this.addressParser = addressParser;
        this.connectionManager = connectionManager;
    }
}
