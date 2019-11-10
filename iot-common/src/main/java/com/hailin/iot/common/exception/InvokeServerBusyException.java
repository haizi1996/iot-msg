package com.hailin.iot.common.exception;


public class InvokeServerBusyException extends RemotingException {
    /** For serialization  */
    private static final long serialVersionUID = 4480283862377034355L;

    /**
     * Default constructor.
     */
    public InvokeServerBusyException() {
    }

    public InvokeServerBusyException(String msg) {
        super(msg);
    }

    public InvokeServerBusyException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
