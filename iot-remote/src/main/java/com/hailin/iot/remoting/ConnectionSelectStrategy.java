package com.hailin.iot.remoting;

import com.hailin.iot.remoting.connection.Connection;

import java.util.List;

/**
 * 选择连接器策略接口
 * @author hailin
 */
public interface ConnectionSelectStrategy {

    Connection select(List<Connection> connections);
}
