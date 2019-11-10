package com.hailin.iot.remoting;


import com.hailin.iot.common.exception.CodecException;
import com.hailin.iot.common.exception.ConnectionClosedException;
import com.hailin.iot.common.exception.InvokeException;
import com.hailin.iot.common.exception.InvokeServerBusyException;
import com.hailin.iot.common.exception.InvokeTimeoutException;
import com.hailin.iot.remoting.future.InvokeCallbackListener;
import com.hailin.iot.remoting.future.InvokeFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;

@Slf4j
public class RpcInvokeCallbackListener implements InvokeCallbackListener {


    private String              address;

    public RpcInvokeCallbackListener() {

    }

    public RpcInvokeCallbackListener(String address) {
        this.address = address;
    }


    @Override
    public void onResponse(InvokeFuture future) {
        InvokeCallback callback = future.getInvokeCallback();
        if (callback != null) {
            CallbackTask task = new CallbackTask(this.getRemoteAddress(), future);
            if (callback.getExecutor() != null) {
                // There is no need to switch classloader, because executor is provided by user.
                try {
                    callback.getExecutor().execute(task);
                } catch (RejectedExecutionException e) {
                    log.warn("Callback thread pool busy.");
                }
            } else {
                task.run();
            }
        }
    }

    class CallbackTask implements Runnable {

        InvokeFuture future;
        String remoteAddress;

        /**
         *
         */
        public CallbackTask(String remoteAddress, InvokeFuture future) {
            this.remoteAddress = remoteAddress;
            this.future = future;
        }

        /**
         * @see Runnable#run()
         */
        @Override
        public void run() {
            InvokeCallback callback = future.getInvokeCallback();
            // a lot of try-catches to protect thread pool
            RpcResponse response = null;


            if (response == null || response.getResponseStatus() != ResponseStatus.SUCCESS) {
                try {
                    Exception e = null;
                    if (response == null) {
                        e = new InvokeException("Exception caught in invocation. The address is "
                                + this.remoteAddress + " responseStatus:");
                    } else {
                        switch (response.getResponseStatus()) {
                            case TIMEOUT:
                                e = new InvokeTimeoutException(
                                        "Invoke timeout when invoke with callback.The address is "
                                                + this.remoteAddress);
                                break;
                            case CONNECTION_CLOSED:
                                e = new ConnectionClosedException(
                                        "Connection closed when invoke with callback.The address is "
                                                + this.remoteAddress);
                                break;
                            case SERVER_THREADPOOL_BUSY:
                                e = new InvokeServerBusyException(
                                        "Server thread pool busy when invoke with callback.The address is "
                                                + this.remoteAddress);
                                break;
                            case SERVER_EXCEPTION:
                                String msg = "Server exception when invoke with callback.Please check the server log! The address is "
                                        + this.remoteAddress;

                                break;
                            default:
                                e = new InvokeException("Exception caught in invocation. The address is "
                                        + this.remoteAddress + " responseStatus:"
                                        + response.getResponseStatus(), future.getCause());

                        }
                    }
                    callback.onException(e);
                } catch (Throwable e) {
                    log.error("Exception occurred in user defined InvokeCallback#onException() logic, The address is {}",
                            this.remoteAddress, e);
                }
            } else {
                ClassLoader oldClassLoader = null;
                try {
                    if (future.getAppClassLoader() != null) {
                        oldClassLoader = Thread.currentThread().getContextClassLoader();
                        Thread.currentThread().setContextClassLoader(future.getAppClassLoader());
                    }
                } catch (CodecException e) {
                    log.error("CodecException caught on when deserialize response in RpcInvokeCallbackListener. The address is {}.",
                            this.remoteAddress, e);
                } catch (Throwable e) {
                    log.error("Exception caught in RpcInvokeCallbackListener. The address is {}",
                            this.remoteAddress, e);
                } finally {
                    if (oldClassLoader != null) {
                        Thread.currentThread().setContextClassLoader(oldClassLoader);
                    }
                } // enf of else
                // end of run
            }
        }
    }

    @Override
    public String getRemoteAddress() {
        return this.address;
    }
}
