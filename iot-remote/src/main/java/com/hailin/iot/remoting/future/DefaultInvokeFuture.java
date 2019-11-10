package com.hailin.iot.remoting.future;

import com.hailin.iot.remoting.InvokeCallback;
import com.hailin.iot.remoting.InvokeContext;
import com.hailin.iot.remoting.RpcResponse;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.Timeout;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public class DefaultInvokeFuture implements InvokeFuture {

    private static final Logger logger = LoggerFactory.getLogger(DefaultInvokeFuture.class);

    private int invokeId;

    private InvokeCallbackListener callbackListener;

    private InvokeCallback callback;

    //相应响应消息
    private volatile MqttMessage responseMessage;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private final AtomicBoolean executeCallbackOnlyOnce = new AtomicBoolean(false);

    private Timeout timeout;

    private Throwable cause;

    private ClassLoader classLoader;

    private InvokeContext invokeContext;

    public DefaultInvokeFuture(int invokeId, InvokeCallbackListener callbackListener,
                               InvokeCallback callback,  MqttMessage request ) {
        this.invokeId = invokeId;
        this.callbackListener = callbackListener;
        this.callback = callback;
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public DefaultInvokeFuture(int invokeId, InvokeCallbackListener callbackListener,
                               InvokeCallback callback, InvokeContext invokeContext ,  MqttMessage request) {
        this(invokeId, callbackListener, callback,request );
        this.invokeContext = invokeContext;
    }






    @Override
    public int invokeId() {
        return 0;
    }

    @Override
    public void executeInvokeCallback() {

    }

    @Override
    public void tryAsyncExecuteInvokeCallbackAbnormally() {

    }

    @Override
    public void setCause(Throwable cause) {

    }

    @Override
    public Throwable getCause() {
        return null;
    }

    @Override
    public InvokeCallback getInvokeCallback() {
        return null;
    }

    @Override
    public void addTimeout(Timeout timeout) {

    }

    @Override
    public void cancelTimeout() {

    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public ClassLoader getAppClassLoader() {
        return null;
    }

    @Override
    public void setInvokeContext(InvokeContext invokeContext) {

    }

    @Override
    public InvokeContext getInvokeContext() {
        return null;
    }

}
