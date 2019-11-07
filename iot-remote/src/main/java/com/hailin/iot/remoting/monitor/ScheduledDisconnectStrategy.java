package com.hailin.iot.remoting.monitor;

import com.hailin.iot.remoting.config.ConfigManager;
import com.hailin.iot.remoting.config.Configs;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.connection.ConnectionPool;
import com.hailin.iot.remoting.task.RunStateRecordedFutureTask;
import com.hailin.iot.remoting.util.FutureTaskUtil;
import com.hailin.iot.remoting.util.RemotingUtil;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ScheduledDisconnectStrategy implements ConnectionMonitorStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledDisconnectStrategy.class);

    private final int connectionThreshold;
    private final Random random;

    public ScheduledDisconnectStrategy() {
        this.connectionThreshold = ConfigManager.conn_threshold();
        this.random = new Random();
    }

    @Override
    public void monitor(Map<String, RunStateRecordedFutureTask<ConnectionPool>> connPools) {
        try {
            if (MapUtils.isEmpty(connPools)) {
                return;
            }

            for (Map.Entry<String, RunStateRecordedFutureTask<ConnectionPool>> entry : connPools
                    .entrySet()) {
                String poolKey = entry.getKey();
                ConnectionPool pool = FutureTaskUtil.getFutureTaskResult(entry.getValue(), logger);

                List<Connection> serviceOnConnections = new ArrayList<Connection>();
                List<Connection> serviceOffConnections = new ArrayList<Connection>();
                for (Connection connection : pool.getAll()) {
                    if (isConnectionOn(connection)) {
                        serviceOnConnections.add(connection);
                    } else {
                        serviceOffConnections.add(connection);
                    }
                }

                if (serviceOnConnections.size() > connectionThreshold) {
                    Connection freshSelectConnect = serviceOnConnections.get(random
                            .nextInt(serviceOnConnections.size()));
                    freshSelectConnect.setAttribute(Configs.CONN_SERVICE_STATUS,
                            Configs.CONN_SERVICE_STATUS_OFF);
                    serviceOffConnections.add(freshSelectConnect);
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("serviceOnConnections({}) size[{}], CONNECTION_THRESHOLD[{}].",
                                poolKey, serviceOnConnections.size(), connectionThreshold);
                    }
                }

                for (Connection offConn : serviceOffConnections) {
                    if (offConn.isInvokeFutureMapFinish()) {
                        if (offConn.isFine()) {
                            offConn.close();
                        }
                    } else {
                        if (logger.isInfoEnabled()) {
                            logger.info("Address={} won't close at this schedule turn",
                                    RemotingUtil.parseRemoteAddress(offConn.getChannel()));
                        }
                    }
                }
            }
        }catch (Exception e){
            logger.error("ScheduledDisconnectStrategy monitor error", e);
        }
    }
    private boolean isConnectionOn(Connection connection) {
        String serviceStatus = (String) connection.getAttribute(Configs.CONN_SERVICE_STATUS);
        return serviceStatus == null || Boolean.parseBoolean(serviceStatus);
    }
}
