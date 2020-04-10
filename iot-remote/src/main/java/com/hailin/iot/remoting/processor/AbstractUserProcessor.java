package com.hailin.iot.remoting.processor;


import com.hailin.iot.remoting.AsyncContext;
import com.hailin.iot.remoting.BizContext;
import com.hailin.iot.remoting.DefaultBizContext;
import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.UserProcessor;
import com.hailin.iot.remoting.util.RemotingUtil;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;


public abstract class AbstractUserProcessor<T extends MqttMessage> implements UserProcessor<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractUserProcessor.class);

    protected ExecutorSelector executorSelector;

    @Override
    public BizContext preHandleRequest(RemotingContext remotingCtx, T request) {
        return new DefaultBizContext(remotingCtx);
    }

    /**
     * By default return null.
     *
     * @see UserProcessor#getExecutor()
     */
    @Override
    public Executor getExecutor() {
        return null;
    }

    /**
     * @see UserProcessor#getExecutorSelector()
     */
    @Override
    public ExecutorSelector getExecutorSelector() {
        return this.executorSelector;
    }


    @Override
    public void setExecutorSelector(ExecutorSelector executorSelector) {
        this.executorSelector = executorSelector;
    }


    @Override
    public boolean processInIOThread() {
        return false;
    }


    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx) {

    }

    @Override
    public Object handleRequest(BizContext bizContext, T request) throws Exception {
        return null;
    }

    @Override
    public MqttMessageType interest() {
        return MqttMessageType.CONNECT;
    }

    @Override
    public boolean timeoutDiscard() {
        return true;
    }

    class ProcessTask implements Runnable {

        RemotingContext   ctx;
        MqttMessage msg;

        public ProcessTask(RemotingContext ctx, MqttMessage msg) {
            this.ctx = ctx;
            this.msg = msg;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                AbstractUserProcessor.this.doProcess(ctx, msg);
            } catch (Throwable e) {
                //protect the thread running this task
                String remotingAddress = RemotingUtil.parseRemoteAddress(ctx.getChannelContext()
                        .channel());
                logger
                        .error(
                                "Exception caught when process rpc request command in RpcRequestProcessor, Id="
                                        + msg.toString() + "! Invoke source address is [" + remotingAddress
                                        + "].", e);
            }
        }

    }

    protected  void doProcess(RemotingContext ctx, MqttMessage msg){

    }
}