package com.hailin.iot.common.remoting;

import com.hailin.iot.common.exception.LifeCycleException;

/**
 * 生命周期的接口
 * @author hailin
 */
public interface LifeCycle {

    void startup() throws LifeCycleException;

    void shutdown() throws LifeCycleException;

    boolean isStarted();
}
