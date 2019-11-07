package com.hailin.iot.remoting.connection;

import com.hailin.iot.remoting.ConnectionSelectStrategy;
import com.hailin.iot.remoting.Scannable;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 连接池
 * @author hailin
 */
public class ConnectionPool implements Scannable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);

    private CopyOnWriteArrayList<Connection> connections;

    private ConnectionSelectStrategy strategy;

    //最后一次访问的时间戳
    private volatile long lastAccessTimeStamp;

    private volatile boolean asyncCreationDone;

    public ConnectionPool(ConnectionSelectStrategy strategy) {
        this.strategy = strategy;
        this.connections = new CopyOnWriteArrayList<>();
        this.asyncCreationDone = true;
    }
    public void removeAndTryClose(Connection connection){
        if (connection ==null){
            return;
        }
        boolean res = connections.remove(connection);
        if (res) {
            connection.decreaseRef();
        }
        if (connection.noRef()) {
            connection.close();
        }
    }

    public Connection get() {
        markAccess();
        if (CollectionUtils.isEmpty(connections)) {
            return null;
        }
        List<Connection> snapshot = new ArrayList<Connection>(connections);
        return strategy.select(snapshot);
    }

    public void add(Connection connection) {
        markAccess();
        if (null == connection) {
            return;
        }
        boolean res = connections.addIfAbsent(connection);
        if (res) {
            connection.increaseRef();
        }
    }

    private void markAccess() {
        lastAccessTimeStamp = System.currentTimeMillis();
    }

    @Override
    public void scan() {
        if (CollectionUtils.isEmpty(connections)){
            return;
        }
        for (Connection connection : connections) {
            if (!connection.isFine()) {
                LOGGER.warn(
                        "Remove bad connection when scanning conns of ConnectionPool - {}:{}",
                        connection.getRemoteIp(), connection.getRemotePort());
                connection.close();
                removeAndTryClose(connection);
            }
        }
    }
    public List<Connection> getAll() {
        markAccess();
        return new ArrayList<>(connections);
    }

    public void removeAllAndTryClose() {
        for (Connection connection : connections) {
            removeAndTryClose(connection);
        }
        connections.clear();
    }

    public int size() {
        return connections.size();
    }

    public void markAsyncCreationStart() {
        asyncCreationDone = false;
    }

    public void markAsyncCreationDone() {
        asyncCreationDone = true;
    }

    public boolean isAsyncCreationDone() {
        return this.asyncCreationDone;
    }
}
