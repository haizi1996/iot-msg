package com.hailin.iot.common.remoting;

import java.util.concurrent.Executor;

/**
 * invoke的 回调接口
 * @author hailin
 */
public interface InvokeCallback {

    void onResponse(final Object result);

    void onException(final Throwable cause);

    Executor getExecutor();
}
