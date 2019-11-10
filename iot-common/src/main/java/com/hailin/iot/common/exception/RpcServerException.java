package com.hailin.iot.common.exception;


public class RpcServerException extends RemotingException {
    /** For serialization  */
    private static final long serialVersionUID = 4480283862377034355L;

    /**
     * Default constructor.
     */
    public RpcServerException() {
    }

    public RpcServerException(String msg) {
        super(msg);
    }

    public RpcServerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
