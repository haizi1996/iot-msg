package com.hailin.iot.common.exception;


public class InvokeServerException extends RemotingException {
    /** For serialization  */
    private static final long serialVersionUID = 4480283862377034355L;

    /**
     * Default constructor.
     */
    public InvokeServerException() {
    }

    public InvokeServerException(String msg) {
        super(msg);
    }

    public InvokeServerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
