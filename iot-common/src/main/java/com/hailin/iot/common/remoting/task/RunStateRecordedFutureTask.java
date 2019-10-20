package com.hailin.iot.common.remoting.task;

import com.hailin.iot.common.exception.FutureTaskNotCompleted;
import com.hailin.iot.common.exception.FutureTaskNotRunYetException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 记录执行的方法运行的状态
 * @param <T>
 */
public class RunStateRecordedFutureTask<T> extends FutureTask<T> {

    private AtomicBoolean hasRun = new AtomicBoolean();

    public RunStateRecordedFutureTask(Callable<T> callable) {
        super(callable);
    }

    @Override
    public void run() {
        hasRun.set(true);
        super.run();
    }

    public T getAfterRun() throws FutureTaskNotRunYetException, FutureTaskNotCompleted, ExecutionException, InterruptedException {
        if (!hasRun.get()) {
            throw new FutureTaskNotRunYetException();
        }

        if (!isDone()) {
            throw new FutureTaskNotCompleted();
        }

        return super.get();
    }
}
