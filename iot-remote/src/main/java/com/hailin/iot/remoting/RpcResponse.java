package com.hailin.iot.remoting;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

@Data
@Getter
@Setter
public class RpcResponse {

    /** For serialization  */
    private static final long serialVersionUID = -5194754228565292441L;
    private ResponseStatus    responseStatus;
    private long              responseTimeMillis;
    private InetSocketAddress responseHost;
    private Throwable         cause;

    public RpcResponse(Throwable cause) {
        this.cause = cause;
    }

    public RpcResponse() {
    }
}
