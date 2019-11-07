package com.hailin.iot.remoting;

import com.hailin.iot.common.exception.LifeCycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务扫描
 * @author hailin
 */
public class RpcTaskScanner extends AbstractLifeCycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcTaskScanner.class);

    private final List<Scannable> scannableList;

    private ScheduledExecutorService scheduledExecutorService;

    public RpcTaskScanner() {
        this.scannableList = new LinkedList<>();
    }

    @Override
    public void startup() throws LifeCycleException {
        super.startup();

        scheduledExecutorService = new ScheduledThreadPoolExecutor(1 , new NamedThreadFactory("RpcTaskScannerThread" , true));

        scheduledExecutorService.scheduleWithFixedDelay(()->{
            for (Scannable scan : scannableList) {
                try{
                    scan.scan();
                }catch (Throwable t){
                    LOGGER.error("Exception caught when scannings.", t);
                }
            }
        } , 10000 , 100000 , TimeUnit.MINUTES);
    }

    public void shutdown() throws LifeCycleException{
        super.shutdown();;
        scheduledExecutorService.shutdown();
    }

    public void add(Scannable target){
        scannableList.add(target);
    }
}
