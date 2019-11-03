package com.hailin.iot.common.remoting.processor;

import com.hailin.iot.common.remoting.RemotingContext;
import com.hailin.iot.common.util.RemotingUtil;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public abstract class AbstractRemotingProcessor<T extends MqttMessage> implements RemotingProcessor<T> {

    private static final Logger logger = LoggerFactory.getLogger("CommonDefault");
    private ExecutorService executor;

    /**
     * Default constructor.
     */
    public AbstractRemotingProcessor() {

    }

    /**
     * Constructor.
     * @param executor ExecutorService
     */
    public AbstractRemotingProcessor(ExecutorService executor) {
        this.executor = executor;
    }


    public abstract void doProcess(RemotingContext ctx, T msg) throws Exception;


    @Override
    public void process(RemotingContext ctx, T msg, ExecutorService defaultExecutor)
            throws Exception {
        ProcessTask task = new ProcessTask(ctx, msg);
        if (this.getExecutor() != null) {
            this.getExecutor().execute(task);
        } else {
            defaultExecutor.execute(task);
        }
    }


    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public void sendResponseIfNecessary(RemotingContext ctx, MqttMessageType messageType, MqttMessage message) {
        throw new RuntimeException();
    }


    public class ProcessTask implements Runnable {

        RemotingContext ctx;
        T               msg;

        public ProcessTask(RemotingContext ctx, T msg) {
            this.ctx = ctx;
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                AbstractRemotingProcessor.this.doProcess(ctx, msg);
            } catch (Throwable e) {
                //protect the thread running this task
                String remotingAddress = RemotingUtil.parseRemoteAddress(ctx.getChannelContext()
                        .channel());
                logger
                        .error(
                                "Exception caught when process rpc request command in AbstractRemotingProcessor, Id="
                                        + msg + "! Invoke source address is [" + remotingAddress
                                        + "].", e);
            }
        }

    }
}
