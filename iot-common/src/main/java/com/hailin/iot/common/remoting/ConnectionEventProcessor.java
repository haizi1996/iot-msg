package com.hailin.iot.common.remoting;

/**
 * 处理连接事件
 * @author hailin
 */
public interface ConnectionEventProcessor {

    void onEvent(String remoteAddress , Connection connection);
}
