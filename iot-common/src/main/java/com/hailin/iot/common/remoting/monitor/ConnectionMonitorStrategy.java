package com.hailin.iot.common.remoting.monitor;

import com.hailin.iot.common.remoting.connection.ConnectionPool;
import com.hailin.iot.common.remoting.task.RunStateRecordedFutureTask;

import java.util.Map;

public interface ConnectionMonitorStrategy {

    void monitor(Map<String, RunStateRecordedFutureTask<ConnectionPool>> connPools);

}
