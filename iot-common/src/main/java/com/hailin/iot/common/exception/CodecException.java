package com.hailin.iot.common.exception;

/**
 * 编码异常
 * @author hailin
 */
public class CodecException extends RemotingException {

    private static final long serialVersionUID = -7513762648815278960L;

    /**
     * Constructor.
     */
    public CodecException() {
    }

    /**
     * Constructor.
     *
     * @param message the detail message.
     */
    public CodecException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
