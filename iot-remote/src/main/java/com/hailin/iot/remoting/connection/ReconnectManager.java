package com.hailin.iot.remoting.connection;

import com.hailin.iot.common.exception.LifeCycleException;
import com.hailin.iot.remoting.AbstractLifeCycle;
import com.hailin.iot.remoting.ConnectionManager;
import com.hailin.iot.remoting.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 重新连接的管理
 * @author hailin
 */
public class ReconnectManager extends AbstractLifeCycle implements Reconnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReconnectManager.class);

    private static final int HEAL_CONNECTION_INTERVAL = 1000;

    private final ConnectionManager connectionManager;

    private final LinkedBlockingQueue<ReconnectTask> tasks;

    private final List<Url> canceled;

    private Thread healConnectionThreads;

    public ReconnectManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.tasks = new LinkedBlockingQueue<>();
        this.canceled = new CopyOnWriteArrayList<>();
    }

    @Override
    public void reconnect(Url url) {
        tasks.add(new ReconnectTask(url));
    }

    @Override
    public void disableReconnect(Url url) {
        canceled.add(url);
    }

    @Override
    public void startup() throws LifeCycleException {
        super.startup();
        this.healConnectionThreads = new Thread(new HealConnectionRunner());
        this.healConnectionThreads.start();
    }

    @Override
    public void shutdown() throws LifeCycleException {
        super.shutdown();
        healConnectionThreads.interrupt();
        this.tasks.clear();
        this.canceled.clear();
    }

    @Override
    public void enableReconnect(Url url) {
        canceled.remove(url);
    }

    private final class HealConnectionRunner implements Runnable{

        private long lastConnectTime = -1;

        @Override
        public void run() {
            while (isStarted()){
                long start = -1;
                ReconnectTask task = null;
                try {
                    if (this.lastConnectTime < HEAL_CONNECTION_INTERVAL){
                        Thread.sleep(HEAL_CONNECTION_INTERVAL);
                    }
                    task = ReconnectManager.this.tasks.take();
                    if (task == null){
                        continue;
                    }
                    start = System.currentTimeMillis();
                    if (!canceled.contains(task.url)){
                        task.run();
                    }else {
                        LOGGER.warn("Invalid reconnect request task {}, cancel list size {}",
                                task.url, canceled.size());
                    }
                    this.lastConnectTime = System.currentTimeMillis();
                }catch (Exception e){
                    if (start != -1){
                        this.lastConnectTime = System.currentTimeMillis() - start;
                    }
                    if (task != null){
                        LOGGER.warn("reconnect target: {} failed.", task.url, e);
                        tasks.add(task);
                    }
                }
            }
        }
    }

    private class ReconnectTask implements Runnable{

        private Url url;

        public ReconnectTask(Url url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                connectionManager.createConnectionAndHealIfNeed(url);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}
