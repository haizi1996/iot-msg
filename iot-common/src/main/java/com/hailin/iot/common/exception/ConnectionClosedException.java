package com.hailin.iot.common.exception;


public class ConnectionClosedException extends RemotingException {

    /** For serialization  */
    private static final long serialVersionUID = -2595820033346329315L;

    /**
     * Default constructor.
     */
    public ConnectionClosedException() {
    }

    public ConnectionClosedException(String msg) {
        super(msg);
    }

    public ConnectionClosedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
