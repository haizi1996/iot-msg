package com.hailin.iot.remoting;

import com.hailin.iot.common.exception.LifeCycleException;
import com.hailin.iot.common.remoting.ConnectionEventHandler;
import com.hailin.iot.common.remoting.ConnectionEventListener;
import com.hailin.iot.common.remoting.ConnectionSelectStrategy;
import com.hailin.iot.common.remoting.DefaultConnectionManager;
import com.hailin.iot.common.remoting.config.switches.GlobalSwitch;
import com.hailin.iot.common.remoting.factory.ConnectionFactory;

public class DefaultClientConnectionManager extends DefaultConnectionManager {


    public DefaultClientConnectionManager(ConnectionSelectStrategy connectionSelectStrategy,
                                          ConnectionFactory connectionFactory,
                                          ConnectionEventHandler connectionEventHandler,
                                          ConnectionEventListener connectionEventListener) {
        super(connectionSelectStrategy, connectionFactory, connectionEventHandler,
                connectionEventListener);
    }

    public DefaultClientConnectionManager(ConnectionSelectStrategy connectionSelectStrategy,
                                          ConnectionFactory connectionFactory,
                                          ConnectionEventHandler connectionEventHandler,
                                          ConnectionEventListener connectionEventListener,
                                          GlobalSwitch globalSwitch) {
        super(connectionSelectStrategy, connectionFactory, connectionEventHandler,
                connectionEventListener, globalSwitch);
    }

    @Override
    public void startup() throws LifeCycleException {
        super.startup();

        this.connectionEventHandler.setConnectionManager(this);
        this.connectionEventHandler.setConnectionEventListener(connectionEventListener);
        this.connectionFactory.init(connectionEventHandler);
    }

}
