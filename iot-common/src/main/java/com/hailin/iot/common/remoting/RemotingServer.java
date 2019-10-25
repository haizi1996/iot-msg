package com.hailin.iot.common.remoting;

import com.hailin.iot.common.remoting.config.Configurable;

import java.util.concurrent.ExecutorService;

/**
 * 远程的Rpc服务接口
 * 预计后续不同的实现
 * @author hailin
 */
public interface RemotingServer extends LifeCycle, Configurable {


    /**
     * 获取服务的IP地址
     *
     * @return ip
     */
    String ip();

    /**
     * Get the port of the server.
     *
     * @return listened port
     */
    int port();

    /**
     * Register processor for command with the command code.
     *
     * @param protocolCode protocol code
     * @param commandCode command code
     * @param processor processor
     */
//    void registerProcessor(byte protocolCode, CommandCode commandCode,
//                           RemotingProcessor<?> processor);

    /**
     * Register default executor service for server.
     *
     * @param protocolCode protocol code
     * @param executor the executor service for the protocol code
     */
    void registerDefaultExecutor(byte protocolCode, ExecutorService executor);

    /**
     * Register user processor.
     *
     * @param processor user processor which can be a single-interest processor or a multi-interest processor
     */
//    void registerUserProcessor(UserProcessor<?> processor);
}
