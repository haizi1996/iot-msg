package com.hailin.iot.remoting.future;

public interface InvokeCallbackListener {

    void onResponse(final InvokeFuture future);

    String getRemoteAddress();
}
