package com.hailin.iot.common.exception;

public class InvokeException extends RemotingException {
    /** For serialization  */
    private static final long serialVersionUID = -3974514863386363570L;

    /**
     * Default constructor.
     */
    public InvokeException() {
    }

    public InvokeException(String msg) {
        super(msg);
    }

    public InvokeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
