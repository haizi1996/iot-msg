package com.hailin.iot.remoting;

import com.hailin.iot.common.exception.LifeCycleException;

public abstract class AbstractLifeCycle implements LifeCycle  {

    private volatile  boolean isStarted = false;

    @Override
    public void startup() throws LifeCycleException {
        if (!isStarted){
            isStarted = true;
            return;
        }
        throw new LifeCycleException("服务早已开始");
    }

    @Override
    public void shutdown() throws LifeCycleException {
        if (isStarted) {
            isStarted = false;
            return;
        }

        throw new LifeCycleException("this component has closed");
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }
}
