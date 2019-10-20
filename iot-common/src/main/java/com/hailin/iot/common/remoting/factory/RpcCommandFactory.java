package com.hailin.iot.common.remoting.factory;

import com.hailin.iot.common.remoting.ResponseStatus;
import com.hailin.iot.common.remoting.command.CommandFactory;
import com.hailin.iot.common.remoting.command.RemotingCommand;

import java.net.InetSocketAddress;

public class RpcCommandFactory implements CommandFactory {

    @Override
    public <T extends RemotingCommand> T createRequestCommand(Object requestObject) {
        return null;
    }

    @Override
    public <T extends RemotingCommand> T createResponseCommand(Object responseObject, RemotingCommand remotingCommand) {
        return null;
    }

    @Override
    public <T extends RemotingCommand> T createExceptionResponse(int id, String errMsg) {
        return null;
    }

    @Override
    public <T extends RemotingCommand> T createExceptionResponse(int id, Throwable t, String errMsg) {
        return null;
    }

    @Override
    public <T extends RemotingCommand> T createExceptionResponse(int id, ResponseStatus status) {
        return null;
    }

    @Override
    public <T extends RemotingCommand> T createExceptionResponse(int id, ResponseStatus status, Throwable t) {
        return null;
    }

    @Override
    public <T extends RemotingCommand> T createTimeoutResponse(InetSocketAddress address) {
        return null;
    }

    @Override
    public <T extends RemotingCommand> T createSendFailedResponse(InetSocketAddress address, Throwable throwable) {
        return null;
    }

    @Override
    public <T extends RemotingCommand> T createConnectionClosedResponse(InetSocketAddress address, String message) {
        return null;
    }
}
