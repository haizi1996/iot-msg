package com.hailin.iot.remoting.monitor;

import com.hailin.iot.remoting.connection.ConnectionPool;
import com.hailin.iot.remoting.task.RunStateRecordedFutureTask;

import java.util.Map;

public interface ConnectionMonitorStrategy {

    void monitor(Map<String, RunStateRecordedFutureTask<ConnectionPool>> connPools);

}
