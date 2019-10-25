package com.hailin.iot.common.remoting.processor;

import com.hailin.iot.common.remoting.NamedThreadFactory;
import com.hailin.iot.common.remoting.config.ConfigManager;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProcessorManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorManager.class);

    private ConcurrentHashMap<MqttMessageType , RemotingProcessor<?>> cmd2processors = new ConcurrentHashMap<>(4);

    private RemotingProcessor<?> defaultProcessor;

    private ExecutorService defaultExecutor;

    private int minPoolSize = ConfigManager.default_tp_min_size();

    private int maxPoolSize = ConfigManager.default_tp_max_size();

    private int queueSize = ConfigManager.default_tp_queue_size();

    private long keepAliveTime  = ConfigManager.default_tp_keepalive_time();

    public ProcessorManager() {
        defaultExecutor = new ThreadPoolExecutor(minPoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize), new NamedThreadFactory(
                "Bolt-default-executor", true));
    }

    public void registerProcessor(MqttMessageType messageType, RemotingProcessor<?> processor) {
        if (this.cmd2processors.containsKey(messageType)) {
            LOGGER.warn("Processor for cmd={} is already registered, the processor is {}, and changed to {}",
                            messageType, cmd2processors.get(messageType).getClass().getName(), processor.getClass()
                                    .getName());
        }
        this.cmd2processors.put(messageType, processor);
    }


    public void registerDefaultProcessor(RemotingProcessor<?> processor) {
        if (this.defaultProcessor == null) {
            this.defaultProcessor = processor;
        } else {
            throw new IllegalStateException("The defaultProcessor has already been registered: "
                    + this.defaultProcessor.getClass());
        }
    }


    public RemotingProcessor<?> getProcessor(MqttMessageType messageType) {
        RemotingProcessor<?> processor = this.cmd2processors.get(messageType);
        if (processor != null) {
            return processor;
        }
        return this.defaultProcessor;
    }


    public ExecutorService getDefaultExecutor() {
        return defaultExecutor;
    }


    public void registerDefaultExecutor(ExecutorService executor) {
        this.defaultExecutor = executor;
    }


}
