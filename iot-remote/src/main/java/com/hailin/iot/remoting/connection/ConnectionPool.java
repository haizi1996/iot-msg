package com.hailin.iot.remoting.connection;

import com.google.common.collect.Lists;
import com.hailin.iot.remoting.ConnectionSelectStrategy;
import com.hailin.iot.remoting.Scannable;
import com.hailin.iot.remoting.future.InvokeFuture;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 连接池
 * @author hailin
 */
public class ConnectionPool implements Scannable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);

    // key 设备类型
    private ConcurrentHashMap<Connection.TermType,  Connection> connections;

    private ConnectionSelectStrategy strategy;

    //最后一次访问的时间戳
    private volatile long lastAccessTimeStamp;

    private Lock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    private volatile boolean asyncCreationDone;

    //已确认的序列号
    private AtomicInteger confirmIndex = new AtomicInteger(0);
    //当前废品的序列号
    private AtomicInteger maxIndex = new AtomicInteger(0);

    private final int region = 5;

    private final ConcurrentHashMap<Integer, InvokeFuture> invokeFutureMap  = new ConcurrentHashMap<Integer, InvokeFuture>(region);

    public Integer getMessageId(){
        int oldMaxIndex = maxIndex.get();
        while (true){
            try {
                if(oldMaxIndex - confirmIndex.get() == 5){
                    condition.await();
                }
                if (maxIndex.compareAndSet(oldMaxIndex , oldMaxIndex + 1)){
                    return maxIndex.get();
                }
                continue;
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public ConnectionPool(ConnectionSelectStrategy strategy) {
        this.strategy = strategy;
        this.connections = new ConcurrentHashMap<>();
        this.asyncCreationDone = true;
    }
//    public void removeAndTryClose(Connection connection){
//        if (connection ==null){
//            return;
//        }
//        boolean res = connections.remove(connection);
//        if (res) {
//            connection.decreaseRef();
//        }
//        if (connection.noRef()) {
//            connection.close();
//        }
//    }

    public Connection get() {
        markAccess();
        if (MapUtils.isEmpty(connections)) {
            return null;
        }
//        List<Connection> snapshot = new ArrayList<Connection>(connections);
        return strategy.select(connections);
    }

    public void add(Connection connection , Connection.TermType type) {
        markAccess();
        if (null == connection) {
            return;
        }
        if(connections.contains(type)){
            connections.get(type).close();
        }
        connections.put(type , connection);
        connection.increaseRef();
    }

    private void markAccess() {
        lastAccessTimeStamp = System.currentTimeMillis();
    }

    @Override
    public void scan() {
        if (MapUtils.isEmpty(connections)){
            return;
        }
        List<Connection.TermType> keys = Lists.newArrayList(connections.keySet());
        for (Connection.TermType type : keys) {
            if (!connections.get(type).isFine()) {
                LOGGER.warn(
                        "Remove bad connection when scanning conns of ConnectionPool - {}:{}",
                        connections.get(type).getRemoteIp(), connections.get(type).getRemotePort());
                connections.get(type).close();
                connections.remove(type);
//                removeAndTryClose(connection);

            }

        }
    }
    public List<Connection> getAll() {
        markAccess();
        return new ArrayList<>(connections.values());
    }

    public void removeAllAndTryClose() {
        for (Connection connection : connections.values()) {
//            removeAndTryClose(connection);
            connection.close();
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

    public boolean isInvokeFutureMapFinish() {
        return invokeFutureMap.isEmpty();
    }

    public InvokeFuture getInvokeFuture(int id) {
        InvokeFuture invokeFuture =  this.invokeFutureMap.get(id);
        return invokeFuture;
    }

    public InvokeFuture addInvokeFuture(InvokeFuture future) {
        return this.invokeFutureMap.putIfAbsent(future.invokeId(), future);
    }

    public synchronized InvokeFuture removeInvokeFuture(int id) {
        for (int i = id + 1; i < maxIndex.get() && invokeFutureMap.contains(i) ; i ++){
            confirmIndex.getAndIncrement();
        }
        return this.invokeFutureMap.remove(id);
    }


    public void onClose() {
        Iterator<Map.Entry<Integer, InvokeFuture>> iter = invokeFutureMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, InvokeFuture> entry = iter.next();
            iter.remove();
            InvokeFuture future = entry.getValue();
            if (future != null) {
                future.cancelTimeout();
                future.tryAsyncExecuteInvokeCallbackAbnormally();
            }
        }
    }

    public boolean isEmpty() {
        return connections.isEmpty();
    }

    public void removeAndTryClose(Connection connection) {
        if (null == connection) {
            return;
        }
        connections.remove(connection.getType());
        connection.decreaseRef();
        if (connection.noRef()) {
            connection.close();
        }
    }

}
