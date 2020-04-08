package com.hailin.iot.remoting.processor;

import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.RpcAsyncContext;
import com.hailin.iot.remoting.UserProcessor;
import com.hailin.iot.remoting.util.RemotingUtil;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

public abstract class AbstractRemotingProcessor<T extends MqttMessage> implements RemotingProcessor<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRemotingProcessor.class);

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


    public void doProcess(RemotingContext ctx, T msg) throws Exception{



        MqttFixedHeader fixedHeader = msg.fixedHeader();
        UserProcessor userProcessor = ctx.getUserProcessor(fixedHeader.messageType());

        long currentTimestamp = System.currentTimeMillis();
        preProcessRemotingContext(ctx, msg, currentTimestamp);

        // 是否要处理 UserProcessor
        if (Objects.isNull(ctx.getUserProcessor(msg.fixedHeader().messageType()))) {
            return;
        }

        // set timeout check state from user's processor
        ctx.setTimeoutDiscard(userProcessor.timeoutDiscard());

        // to check whether to process in io thread
        if (userProcessor.processInIOThread()) {

            // process in io thread
//            new ProcessTask(ctx, msg).run();
            dispatchToUserProcessor(ctx , msg);
            return;// end
        }

        Executor executor;
        // to check whether get executor using executor selector
        if (null == userProcessor.getExecutorSelector()) {
            executor = userProcessor.getExecutor();
        } else {

            //try get executor with strategy
            executor = userProcessor.getExecutorSelector().select(fixedHeader.messageType(),
                    msg);
        }

        // Till now, if executor still null, then try default
        if (executor == null) {
            dispatchToUserProcessor(ctx , msg);
        }else {
            // use the final executor dispatch process task
            executor.execute(() -> dispatchToUserProcessor(ctx , msg));
        }

//        if (ctx.isTimeoutDiscard() && ctx.isRequestTimeout()) {
//            timeoutLog(cmd, currentTimestamp, ctx);// do some log
//            return;// then, discard this request
//        }
    }

    private void dispatchToUserProcessor(RemotingContext ctx, T msg) {
        // processor here must not be null, for it have been checked before
        UserProcessor processor = ctx.getUserProcessor(msg.fixedHeader().messageType());
        if (processor instanceof AsyncUserProcessor) {
            try {
                processor.handleRequest(processor.preHandleRequest(ctx, msg),
                        new RpcAsyncContext(ctx, msg, this));
            } catch (RejectedExecutionException e) {
                logger
                        .warn("RejectedExecutionException occurred when do ASYNC process in RpcRequestProcessor");
//                sendResponseIfNecessary(ctx, type, this.getCommandFactory()
//                        .createExceptionResponse(id, ResponseStatus.SERVER_THREADPOOL_BUSY));
            } catch (Throwable t) {
                String errMsg = "AYSNC process rpc request failed in RpcRequestProcessor, id=" + msg.toString();
                logger.error(errMsg, t);
//                sendResponseIfNecessary(ctx, type, this.getCommandFactory()
//                        .createExceptionResponse(id, t, errMsg));
            }
        } else {
            try {
                Object responseObject = processor
                        .handleRequest(processor.preHandleRequest(ctx, msg),
                                msg);

//                sendResponseIfNecessary(ctx, type,
//                        this.getCommandFactory().createResponse(responseObject, cmd));
            } catch (RejectedExecutionException e) {
                logger
                        .warn("RejectedExecutionException occurred when do SYNC process in RpcRequestProcessor");
//                sendResponseIfNecessary(ctx, type, this.getCommandFactory()
//                        .createExceptionResponse(id, ResponseStatus.SERVER_THREADPOOL_BUSY));
            } catch (Throwable t) {
                String errMsg = "SYNC process rpc request failed in RpcRequestProcessor, id=" + msg.toString();
                logger.error(errMsg, t);
//                sendResponseIfNecessary(ctx, type, this.getCommandFactory()
//                        .createExceptionResponse(id, t, errMsg));
            }
        }
    }

    protected abstract void preProcessRemotingContext(RemotingContext ctx, T msg, long currentTimestamp)throws Exception;


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
