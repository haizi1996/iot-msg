package com.hailin.iot.remoting.handler;

import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.processor.RemotingProcessor;

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
