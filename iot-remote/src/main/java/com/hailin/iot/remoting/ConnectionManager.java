package com.hailin.iot.remoting;


import com.hailin.iot.common.exception.RemotingException;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.connection.ConnectionPool;

import java.util.List;
import java.util.Map;

/**
 * 连接管理接口
 * @author zhanghailin
 */
public interface ConnectionManager  extends Scannable, LifeCycle {

    /**
     * 增加一个连接
     * @param connection 连接
     */
    void add(Connection connection);


    /**
     * 增加一个连接给特定的池
     * @param connection 连接
     * @param poolKey 池的key
     */
    void add(Connection connection, String poolKey);

    /**
     * 从特定的池中获取连接
     * @param poolKey 池的key
     */
    Connection get(String poolKey);


    ConnectionPool getConnectionPool(String poolKey);

    List<Connection> getAll(String poolKey);

    Map<String, List<Connection>> getAll();

    void remove(Connection connection);

    void remove(Connection connection, String poolKey);

    void remove(String poolKey);

    void check(Connection connection) throws RemotingException;

    Connection create(Url url ) throws RemotingException;

    Connection getAndCreateIfAbsent(Url url ) throws InterruptedException, RemotingException;

    Connection create(String address, int connectTimeout ) throws RemotingException;

    void createConnectionAndHealIfNeed(Url url ) throws InterruptedException, RemotingException;
    /**
     * 统计特定池里的连接
     * @param poolKey 池的key
     */
    int count(String poolKey);

}
