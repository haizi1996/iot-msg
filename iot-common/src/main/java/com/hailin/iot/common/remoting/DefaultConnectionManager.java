package com.hailin.iot.common.remoting;

import com.hailin.iot.common.exception.LifeCycleException;
import com.hailin.iot.common.exception.RemotingException;
import com.hailin.iot.common.remoting.config.ConfigManager;
import com.hailin.iot.common.remoting.config.switches.GlobalSwitch;
import com.hailin.iot.common.remoting.connection.Connection;
import com.hailin.iot.common.remoting.connection.ConnectionPool;
import com.hailin.iot.common.remoting.factory.ConnectionFactory;
import com.hailin.iot.common.remoting.task.RunStateRecordedFutureTask;
import com.hailin.iot.common.util.FutureTaskUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Getter
@Setter
public class DefaultConnectionManager extends AbstractLifeCycle implements ConnectionManager , Scannable , ConnectionHeartbeatManager , LifeCycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConnectionManager.class);

    public static final int DEFAULT_EXPIRE_TIME = 10 * 60 * 1000;

    /**
     * default retry times when failed to get result of FutureTask
     */
    public static final int DEFAULT_RETRY_TIMES = 2;

    //异步的线程池
    private ThreadPoolExecutor asyncCreateConnectionExecutor;

    //开关状态
    private GlobalSwitch globalSwitch;

    protected ConcurrentHashMap<String , RunStateRecordedFutureTask<ConnectionPool>> connTasks;

    protected ConcurrentHashMap<String , FutureTask<Integer>> healTasks;

    //连接选择策略
    protected ConnectionSelectStrategy connectionSelectStrategy;

    //远程地址解析器
    protected RemotingAddressParser addressParser;

    //连接工厂
    protected ConnectionFactory connectionFactory;

    protected ConnectionEventHandler connectionEventHandler;

    public ConnectionEventListener connectionEventListener;

    public DefaultConnectionManager() {
        this.healTasks = new ConcurrentHashMap<>();
        healTasks = new ConcurrentHashMap<>();

    }

    public DefaultConnectionManager(ConnectionSelectStrategy connectionSelectStrategy) {
        this();
        this.connectionSelectStrategy = connectionSelectStrategy;
    }

    public DefaultConnectionManager(ConnectionSelectStrategy connectionSelectStrategy, ConnectionFactory connectionFactory) {
        this(connectionSelectStrategy);
        this.connectionFactory = connectionFactory;
    }

    public DefaultConnectionManager(RemotingAddressParser addressParser, ConnectionFactory connectionFactory, ConnectionEventHandler connectionEventHandler) {
        this.addressParser = addressParser;
        this.connectionFactory = connectionFactory;
        this.connectionEventHandler = connectionEventHandler;
    }

    public DefaultConnectionManager(ConnectionSelectStrategy connectionSelectStrategy,
                                    ConnectionFactory connectionFactory,
                                    ConnectionEventHandler connectionEventHandler,
                                    ConnectionEventListener connectionEventListener) {
        this(connectionSelectStrategy, connectionFactory);
        this.connectionEventHandler = connectionEventHandler;
        this.connectionEventListener = connectionEventListener;
    }

    public DefaultConnectionManager(ConnectionSelectStrategy connectionSelectStrategy,
                                    ConnectionFactory connectionFactory,
                                    ConnectionEventHandler connectionEventHandler,
                                    ConnectionEventListener connectionEventListener,
                                    GlobalSwitch globalSwitch) {
        this(connectionSelectStrategy, connectionFactory, connectionEventHandler,
                connectionEventListener);
        this.globalSwitch = globalSwitch;
    }


    @Override
    public void startup() throws LifeCycleException {
        super.startup();

        long keepAliveTime = ConfigManager.conn_create_tp_keepalive();
        int queueSize = ConfigManager.conn_create_tp_queue_size();
        int minPoolSize = ConfigManager.conn_create_tp_min_size();
        int maxPoolSize = ConfigManager.conn_create_tp_max_size();
        this.asyncCreateConnectionExecutor = new ThreadPoolExecutor(minPoolSize , maxPoolSize, keepAliveTime , TimeUnit.SECONDS , new ArrayBlockingQueue<>(queueSize) , new NamedThreadFactory("iot-conn-warmup-executor" , true));
    }

    @Override
    public void shutdown() throws LifeCycleException {
        super.shutdown();

        if (asyncCreateConnectionExecutor != null){
            asyncCreateConnectionExecutor.shutdown();
        }
        if (MapUtils.isEmpty(connTasks)){
            return;
        }
        Iterator<String> iterator = this.connTasks.keySet().iterator();
        while (iterator.hasNext()){
            String poolKey = iterator.next();
            this.remove(poolKey);
            iterator.remove();
        }
        LOGGER.warn("All connection pool and connections have been removed!");
    }

    @Override
    public void add(Connection connection) {
        Set<String> poolKeys = connection.getPoolKeys();
        for (String poolKey :poolKeys){
            this.add(connection , poolKey);
        }
    }

    @Override
    public void add(Connection connection, String poolKey) {
        ConnectionPool pool = null;
        try {
            // get or create an empty connection pool
            pool = this.getConnectionPoolAndCreateIfAbsent(poolKey, new ConnectionPoolCall());
        } catch (Exception e) {
            // should not reach here.
            LOGGER.error(
                    "[NOTIFYME] Exception occurred when getOrCreateIfAbsent an empty ConnectionPool!",
                    e);
        }
        if (pool != null) {
            pool.add(connection);
        } else {
            // should not reach here.
            LOGGER.error("[NOTIFYME] Connection pool NULL!");
        }
    }

    /**
     * 创建 连接池实例
     * @param poolKey 池的key
     * @param connectionPoolCall
     */
    private ConnectionPool getConnectionPoolAndCreateIfAbsent(String poolKey, ConnectionPoolCall connectionPoolCall) throws InterruptedException {
        RunStateRecordedFutureTask<ConnectionPool> initialTask;
        ConnectionPool pool = null;
        int retry = DEFAULT_RETRY_TIMES, timesOfResultNull = 0, timesOfInterrupt = 0;

        for (int i = 0; i < retry && Objects.isNull(pool); i++) {
            initialTask = this.connTasks.get(poolKey);
            if (initialTask == null) {
                RunStateRecordedFutureTask<ConnectionPool> newTask = new RunStateRecordedFutureTask<>(connectionPoolCall);
                initialTask = this.connTasks.putIfAbsent(poolKey, newTask);
                if (Objects.isNull(initialTask)) {
                    initialTask = newTask;
                    initialTask.run();
                }
            }

            try {
                pool = initialTask.get();
                if (null == pool) {
                    if (i + 1 < retry) {
                        timesOfResultNull++;
                        continue;
                    }
                    this.connTasks.remove(poolKey);
                    String errMsg = "Get future task result null for poolKey [" + poolKey
                            + "] after [" + (timesOfResultNull + 1) + "] times try.";
                    throw new RemotingException(errMsg);
                }
            } catch (InterruptedException e) {
                if (i + 1 < retry) {
                    timesOfInterrupt++;
                    continue;// retry if interrupted
                }
                this.connTasks.remove(poolKey);
                LOGGER.warn("Future task of poolKey {} interrupted {} times. InterruptedException thrown and stop retry.",
                        poolKey, (timesOfInterrupt + 1), e);
                throw e;
            } catch (ExecutionException e) {
                // DO NOT retry if ExecutionException occurred
                this.connTasks.remove(poolKey);

                Throwable cause = e.getCause();
                if (cause instanceof RemotingException) {
                    throw (RemotingException) cause;
                } else {
                    FutureTaskUtil.launderThrowable(cause);
                }
            }
        }
        return pool;
    }

    @Override
    public Connection get(String poolKey) {
        ConnectionPool pool = this.getConnectionPool(this.connTasks.get(poolKey));
        return null == pool ? null : pool.get();
    }

    /**
     * 获取连接池
     */
    private ConnectionPool getConnectionPool(RunStateRecordedFutureTask<ConnectionPool> task) {
        return FutureTaskUtil.getFutureTaskResult(task, LOGGER);

    }

    @Override
    public List<Connection> getAll(String poolKey) {
        ConnectionPool pool = this.getConnectionPool(this.connTasks.get(poolKey));
        return null == pool ? new ArrayList<Connection>() : pool.getAll();
    }

    @Override
    public void disableHeartbeat(Connection connection) {

    }

    @Override
    public void enableHeartbeat(Connection connection) {

    }

    @Override
    public Map<String, List<Connection>> getAll() {
        return null;
    }

    @Override
    public void remove(Connection connection) {

    }

    @Override
    public void remove(Connection connection, String poolKey) {

    }

    @Override
    public void remove(String poolKey) {

    }

    @Override
    public void check(Connection connection) throws RemotingException {

    }

    @Override
    public int count(String poolKey) {
        return 0;
    }

    @Override
    public void scan() {

    }

    private class ConnectionPoolCall implements Callable<ConnectionPool>{

        private boolean whetherInitConnection;

        private Url url;

        public ConnectionPoolCall() {
            this.whetherInitConnection = false;
        }

        public ConnectionPoolCall(Url url) {
            this.url = url;
            this.whetherInitConnection = true;
        }

        @Override
        public ConnectionPool call() throws Exception {
            final ConnectionPool pool = new ConnectionPool(connectionSelectStrategy);
            if (whetherInitConnection){
                try {
                    doCreate(this.url , pool , this.getClass().getSimpleName(), 1);
                }catch (Exception e){
                    pool.removeAllAndTryClose();
                    throw e;
                }
            }
            return pool;
        }
    }

    private void doCreate(final Url url, final ConnectionPool pool , final String taskName, final int syncCreateNumWhenNotWarmup) {
        final int actualNum = pool.size() , expectNum = url.getConnNum();
        if (actualNum > expectNum){
            return;
        }
        LOGGER.debug("actual num {}, expect num {}, task name {}", actualNum, expectNum,
                taskName);
        if (url.isConnWarmup()){
            for (int i = actualNum ; i < expectNum ; i ++){
                Connection connection = create(url);
                pool.add(connection);
            }
        }else {
            if (syncCreateNumWhenNotWarmup < 0 || syncCreateNumWhenNotWarmup > url.getConnNum()){
                throw new IllegalArgumentException(
                        "sync create number when not warmup should be [0," + url.getConnNum() + "]");
            }
            // 同步的方式创建连接
            if (syncCreateNumWhenNotWarmup > 0) {
                for (int i = 0; i < syncCreateNumWhenNotWarmup; ++i) {
                    Connection connection = create(url);
                    pool.add(connection);
                }
                if (syncCreateNumWhenNotWarmup >= url.getConnNum()) {
                    return;
                }
            }
            pool.markAsyncCreationStart();
            try {
                this.asyncCreateConnectionExecutor.execute(() -> {
                    try {
                    for (int i = pool.size(); i < url.getConnNum(); i++) {
                        Connection connection = null;
                        try {
                            connection = create(url);
                        } catch (RemotingException e) {
                            LOGGER.error("Exception occurred in async create connection thread for {}, taskName {}",
                                    url.getUniqueKey(), taskName, e);
                        }
                        pool.add(connection);
                    }
                }finally{
                        pool.markAsyncCreationDone();
                }
            });
            } catch (RejectedExecutionException e){
                pool.markAsyncCreationDone();// mark the end of async when reject
                throw e;
            }
        }
    }

    @Override
    public Connection getAndCreateIfAbsent(Url url) throws InterruptedException, RemotingException {
        ConnectionPool connectionPool = this.getConnectionPoolAndCreateIfAbsent(url.getUniqueKey() , new ConnectionPoolCall(url));
        if (Objects.isNull(connectionPool)){
            return connectionPool.get();
        }else {
            LOGGER.error("[NOTIFYME] bug detected! pool here must not be null!");
            return null;
        }
    }

    @Override
    public void createConnectionAndHealIfNeed(Url url) throws InterruptedException, RemotingException {
        ConnectionPool pool = this.getConnectionPoolAndCreateIfAbsent(url.getUniqueKey(),
                new ConnectionPoolCall(url));
        if (null != pool) {
            healIfNeed(pool, url);
        } else {
            LOGGER.error("[NOTIFYME] bug detected! pool here must not be null!");
        }
    }

    private void healIfNeed(ConnectionPool pool, Url url) throws InterruptedException {
        String poolKey = url.getUniqueKey();
        // only when async creating connections done
        // and the actual size of connections less than expected, the healing task can be run.
        if (pool.isAsyncCreationDone() && pool.size() < url.getConnNum()) {
            FutureTask<Integer> task = this.healTasks.get(poolKey);
            if (null == task) {
                FutureTask<Integer> newTask = new FutureTask<Integer>(new HealConnectionCall(url,
                        pool));
                task = this.healTasks.putIfAbsent(poolKey, newTask);
                if (null == task) {
                    task = newTask;
                    task.run();
                }
            }
            try {
                int numAfterHeal = task.get();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("[NOTIFYME] - conn num after heal {}, expected {}, warmup {}",
                            numAfterHeal, url.getConnNum(), url.isConnWarmup());
                }
            } catch (InterruptedException e) {
                this.healTasks.remove(poolKey);
                throw e;
            } catch (ExecutionException e) {
                this.healTasks.remove(poolKey);
                Throwable cause = e.getCause();
                if (cause instanceof RemotingException) {
                    throw (RemotingException) cause;
                } else {
                    FutureTaskUtil.launderThrowable(cause);
                }
            }
            // heal task is one-off, remove from cache directly after run
            this.healTasks.remove(poolKey);
        }
    }
    @AllArgsConstructor
    private class HealConnectionCall implements Callable<Integer>{

        private Url url;
        private ConnectionPool pool;


        @Override
        public Integer call() throws Exception {
            doCreate(url , this.pool , this.getClass().getSimpleName() , 0);
            return pool.size();
        }
    }

    @Override
    public Connection create(Url url) throws RemotingException {
        Connection conn;
        try {
            conn = this.connectionFactory.createConnection(url);
        } catch (Exception e) {
            throw new RemotingException("Create connection failed. The address is "
                    + url.getOriginUrl(), e);
        }
        return conn;
    }

    public ConcurrentHashMap<String, RunStateRecordedFutureTask<ConnectionPool>> getConnPools() {
        return this.connTasks;
    }
}

