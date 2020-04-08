package com.hailin.iot.remoting;

import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.connection.ConnectionPool;

/**
 * 业务上下文接口
 * @author hailin
 */
public interface BizContext {

    String getRemoteAddress();

    String getRemoteHost();

    Integer getRemotePort();

    Connection getConnection();

    ConnectionPool getConnectionPool();

    boolean isRequestTimeout();

    int getClientTimeout();

    long getArriveTimestamp();

    void put(String key, String value);

    String get(String key);

    InvokeContext getInvokeContext();

    RemotingContext getRemotingCtx();
}
