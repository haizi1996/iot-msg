package com.hailin.iot.remoting.factory;

import com.hailin.iot.remoting.ConnectionEventHandler;
import com.hailin.iot.remoting.ConnectionManager;
import com.hailin.iot.remoting.Url;
import com.hailin.iot.remoting.connection.Connection;

/**
 * 连接工厂
 * @author hailin
 */
public interface ConnectionFactory {

    void init(ConnectionEventHandler connectionEventHandler);

    Connection createConnection(Url url , ConnectionManager connectionManager) throws Exception;

    Connection createConnection(String targetIp , int targetPort , int connectTimeOut ,   ConnectionManager connectionManager) throws Exception;

}
