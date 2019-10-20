package com.hailin.iot.common.remoting;

import com.hailin.iot.common.remoting.connection.Connection;

/**
 * 业务上下文接口
 * @author hailin
 */
public interface BizContext {

    String getRemoteAddress();

    String getRemoteHost();

    String getRemotePort();

    Connection getConnection();

    boolean isRequestTimeout();

    int getClientTimeout();

    long getArriveTimestamp();

    void put(String key, String value);

    String get(String key);

    InvokeContext getInvokeContext();
}