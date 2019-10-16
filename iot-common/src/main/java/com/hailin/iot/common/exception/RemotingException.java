package com.hailin.iot.common.exception;

/**
 * 远程异常
 * @author hailin
 */
public class RemotingException extends RuntimeException {
    /** For serialization */
    private static final long serialVersionUID = 6183635628271812505L;

    /**
     * Constructor.
     */
    public RemotingException() {

    }

    /**
     * Constructor.
     */
    public RemotingException(String message) {
        super(message);
    }

    /**
     * Constructor.
     */
    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }
}
