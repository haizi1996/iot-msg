package com.hailin.iot.common.remoting.factory;

import com.hailin.iot.common.remoting.ConnectionEventHandler;
import com.hailin.iot.common.remoting.Url;
import com.hailin.iot.common.remoting.connection.Connection;

/**
 * 连接工厂
 * @author hailin
 */
public interface ConnectionFactory {

    void init(ConnectionEventHandler connectionEventHandler);

    Connection createConnection(Url url) throws Exception;

    Connection createConnection(String targetIp , int targetPort , int connectTimeOut) throws Exception;

}
