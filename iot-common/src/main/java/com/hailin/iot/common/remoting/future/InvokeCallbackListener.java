package com.hailin.iot.common.remoting.future;

public interface InvokeCallbackListener {

    void onResponse(final InvokeFuture future);

    String getRemoteAddress();
}
