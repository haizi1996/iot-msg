package com.hailin.iot.remoting.connection;

import com.hailin.iot.remoting.ConnectionSelectStrategy;
import com.hailin.iot.remoting.Scannable;
import com.hailin.iot.remoting.future.InvokeFuture;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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

    private CopyOnWriteArrayList<Connection> connections;

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
}
