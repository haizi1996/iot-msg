package com.hailin.iot.remoting;

import com.hailin.iot.remoting.connection.Connection;

/**
 * 处理连接事件
 * @author hailin
 */
public interface ConnectionEventProcessor {

    void onEvent(String remoteAddress , Connection connection);
}
