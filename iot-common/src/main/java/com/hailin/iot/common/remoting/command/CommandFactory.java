package com.hailin.iot.common.remoting.command;

import com.hailin.iot.common.remoting.ResponseStatus;

import java.net.InetSocketAddress;

/**
 * 命令工厂
 * @author hailin
 */
public interface CommandFactory {

    <T extends RemotingCommand> T createRequestCommand(final Object requestObject);

    <T extends RemotingCommand> T createResponseCommand(final Object responseObject , RemotingCommand remotingCommand);

    <T extends RemotingCommand> T createExceptionResponse(int id, String errMsg);

    <T extends RemotingCommand> T createExceptionResponse(int id, final Throwable t, String errMsg);

    <T extends RemotingCommand> T createExceptionResponse(int id, ResponseStatus status);

    <T extends RemotingCommand> T createExceptionResponse(int id, ResponseStatus status,
                                                          final Throwable t);

    <T extends RemotingCommand> T createTimeoutResponse(final InetSocketAddress address);

    <T extends RemotingCommand> T createSendFailedResponse(final InetSocketAddress address,
                                                           Throwable throwable);

    <T extends RemotingCommand> T createConnectionClosedResponse(final InetSocketAddress address,
                                                                 String message);
}
