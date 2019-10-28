package com.hailin.iot.common.remoting.future;

import com.hailin.iot.common.remoting.InvokeCallback;
import com.hailin.iot.common.remoting.InvokeContext;
import com.hailin.iot.common.remoting.command.RemotingCommand;
import com.hailin.iot.common.remoting.command.ResponseCommand;
import io.netty.util.Timeout;

import java.net.InetSocketAddress;

public interface InvokeFuture {

    ResponseCommand waitResponse(final long timeoutMillis ) throws InterruptedException;

    RemotingCommand waitResponse() throws InterruptedException;

    RemotingCommand createConnectionClosedResponse(InetSocketAddress address);

    RemotingCommand putResponse(final RemotingCommand response);


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