package com.hailin.iot.remoting.processor;


import com.hailin.iot.remoting.NamedThreadFactory;
import com.hailin.iot.remoting.UserProcessor;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class DefaultExecutorSelector implements UserProcessor.ExecutorSelector {
    public static final String EXECUTOR0 = "executor0";
    public static final String EXECUTOR1 = "executor1";
    private String             chooseExecutorStr;
    /** executor */
    private ThreadPoolExecutor executor0;
    private ThreadPoolExecutor executor1;

    public DefaultExecutorSelector(String chooseExecutorStr) {
        this.chooseExecutorStr = chooseExecutorStr;
        this.executor0 = new ThreadPoolExecutor(1, 3, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(4), new NamedThreadFactory("Rpc-specific0-executor"));
        this.executor1 = new ThreadPoolExecutor(1, 3, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(4), new NamedThreadFactory("Rpc-specific1-executor"));
    }

    @Override
    public Executor select(MqttMessageType messageType, Object requestHeader) {
        if (Objects.equals(chooseExecutorStr, requestHeader)) {
            return executor1;
        } else {
            return executor0;
        }
    }
}