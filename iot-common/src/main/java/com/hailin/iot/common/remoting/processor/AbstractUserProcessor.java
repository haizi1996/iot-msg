package com.hailin.iot.common.remoting.processor;


import com.hailin.iot.common.remoting.BizContext;
import com.hailin.iot.common.remoting.DefaultBizContext;
import com.hailin.iot.common.remoting.RemotingContext;
import com.hailin.iot.common.remoting.UserProcessor;

import java.util.concurrent.Executor;


public abstract class AbstractUserProcessor<T> implements UserProcessor<T> {

    protected ExecutorSelector executorSelector;

    @Override
    public BizContext preHandleRequest(RemotingContext remotingCtx, T request) {
        return new DefaultBizContext(remotingCtx);
    }

    /**
     * By default return null.
     *
     * @see UserProcessor#getExecutor()
     */
    @Override
    public Executor getExecutor() {
        return null;
    }

    /**
     * @see UserProcessor#getExecutorSelector()
     */
    @Override
    public ExecutorSelector getExecutorSelector() {
        return this.executorSelector;
    }


    @Override
    public void setExecutorSelector(ExecutorSelector executorSelector) {
        this.executorSelector = executorSelector;
    }


    @Override
    public boolean processInIOThread() {
        return false;
    }


    @Override
    public boolean timeoutDiscard() {
        return true;
    }
}