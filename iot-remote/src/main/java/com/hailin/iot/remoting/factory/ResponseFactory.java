package com.hailin.iot.remoting.factory;

import com.hailin.iot.common.exception.RpcServerException;
import com.hailin.iot.remoting.ResponseStatus;
import com.hailin.iot.remoting.RpcResponse;


public class ResponseFactory {

    public static RpcResponse createResponse(final Object responseObject) {
        RpcResponse response = new RpcResponse( );
        response.setResponseStatus(ResponseStatus.SUCCESS);
        return response;
    }

    public static RpcResponse createExceptionResponse( String errMsg) {
        return createExceptionResponse( null, errMsg);
    }

    public static RpcResponse createExceptionResponse(final Throwable t, String errMsg) {
        RpcResponse response = null;
        if (null == t) {
            response = new RpcResponse( createServerException(errMsg));
        } else {
            response = new RpcResponse( createServerException(t, errMsg));
        }
        response.setResponseStatus(ResponseStatus.SERVER_EXCEPTION);
        return response;
    }

    public static RpcResponse createExceptionResponse( ResponseStatus status) {
        RpcResponse responseCommand = new RpcResponse();
        responseCommand.setResponseStatus(status);
        return responseCommand;
    }

    public static RpcResponse createExceptionResponse( ResponseStatus status, Throwable t) {
        RpcResponse responseCommand = createExceptionResponse(status);
        return responseCommand;
    }
    private static RpcServerException createServerException(String errMsg) {
        return new RpcServerException(errMsg);
    }
    private static RpcServerException createServerException(Throwable t, String errMsg) {
        String formattedErrMsg = String.format(
                "[Server]OriginErrorMsg: %s: %s. AdditionalErrorMsg: %s", t.getClass().getName(),
                t.getMessage(), errMsg);
        RpcServerException e = new RpcServerException(formattedErrMsg);
        e.setStackTrace(t.getStackTrace());
        return e;
    }
}
