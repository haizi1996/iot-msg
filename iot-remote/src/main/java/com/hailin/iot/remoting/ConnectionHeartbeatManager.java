package com.hailin.iot.remoting;

import com.hailin.iot.remoting.connection.Connection;

/**
 * 心跳连接管理
 * @author hailin
 */
public interface ConnectionHeartbeatManager {

    /**
     * 禁用连接的心跳管理
     * @param connection 连接
     */
    void disableHeartbeat(Connection connection);

    /**
     * 启用连接心跳的管理
     * @param connection 连接
     */
    void enableHeartbeat(Connection connection);


}
