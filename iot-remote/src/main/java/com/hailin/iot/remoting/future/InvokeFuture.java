package com.hailin.iot.remoting.future;

import com.hailin.iot.remoting.InvokeCallback;
import com.hailin.iot.remoting.InvokeContext;
import io.netty.util.Timeout;


public interface InvokeFuture {


    int invokeId();

    void executeInvokeCallback();

    void tryAsyncExecuteInvokeCallbackAbnormally();

    void setCause(Throwable cause);

    void getCause();

    InvokeCallback getInvokeCallback();

    void addTimeout(Timeout timeout);

    void cancelTimeout();

    boolean isDone();

    ClassLoader getAppClassLoader();

    void setInvokeContext(InvokeContext invokeContext);

    InvokeContext getInvokeContext();
}
