package com.hailin.iot.common.exception;


public class InvokeSendFailedException extends RemotingException {

    /** For serialization  */
    private static final long serialVersionUID = 4832257777758730796L;

    /**
     * Default constructor.
     */
    public InvokeSendFailedException() {
    }

    public InvokeSendFailedException(String msg) {
        super(msg);
    }

    public InvokeSendFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
