package com.hailin.iot.common.remoting.handler;

import com.hailin.iot.common.remoting.RemotingContext;
import com.hailin.iot.common.remoting.processor.RemotingProcessor;

import java.util.concurrent.ExecutorService;

/**
 * 命令处理器
 * @author hailin
 */
public interface CommandHandler {

    void handleCommand(RemotingContext ctx , Object msg);

    void registerProcessor( RemotingProcessor<?> processor);

    void registerDefaultExecutor(ExecutorService executor);

    ExecutorService getDefaultExecutor();
}
