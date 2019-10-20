package com.hailin.iot.broker.remoting;

import com.hailin.iot.common.remoting.ConnectionSelectStrategy;
import com.hailin.iot.common.remoting.DefaultConnectionManager;

public class DefaultServerConnectionManager extends DefaultConnectionManager implements ServerConnectionManager{
    public DefaultServerConnectionManager(ConnectionSelectStrategy connectionSelectStrategy) {
        super(connectionSelectStrategy);
    }
}
