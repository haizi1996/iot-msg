package com.hailin.iot.common.remoting.monitor;

import com.hailin.iot.common.exception.LifeCycleException;
import com.hailin.iot.common.remoting.AbstractLifeCycle;
import com.hailin.iot.common.remoting.DefaultConnectionManager;
import com.hailin.iot.common.remoting.NamedThreadFactory;
import com.hailin.iot.common.remoting.config.ConfigManager;
import com.hailin.iot.common.remoting.connection.ConnectionPool;
import com.hailin.iot.common.remoting.task.RunStateRecordedFutureTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultConnectionMonitor extends AbstractLifeCycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConnectionMonitor.class);

    private final DefaultConnectionManager connectionManager;
    private final ConnectionMonitorStrategy strategy;

    private ScheduledThreadPoolExecutor executor;

    public DefaultConnectionMonitor(ConnectionMonitorStrategy strategy,
                                    DefaultConnectionManager connectionManager) {
        if (strategy == null) {
            throw new IllegalArgumentException("null strategy");
        }

        if (connectionManager == null) {
            throw new IllegalArgumentException("null connectionManager");
        }

        this.strategy = strategy;
        this.connectionManager = connectionManager;
    }

    @Override
    public void startup() throws LifeCycleException {
        super.startup();

        long initialDelay = ConfigManager.conn_monitor_initial_delay();

        /* period of schedule task, unit: ms*/
        long period = ConfigManager.conn_monitor_period();

        this.executor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("ConnectionMonitorThread", true), new ThreadPoolExecutor.AbortPolicy());
        this.executor.scheduleAtFixedRate(() -> {
            try {
                Map<String, RunStateRecordedFutureTask<ConnectionPool>> connPools = connectionManager.getConnPools();
                strategy.monitor(connPools);
            } catch (Exception e) {
                LOGGER.warn("MonitorTask error", e);
            }

        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdown() throws LifeCycleException {
        super.shutdown();
        executor.purge();
        executor.shutdown();
    }


}
