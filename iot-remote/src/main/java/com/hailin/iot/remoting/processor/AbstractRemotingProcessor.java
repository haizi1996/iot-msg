package com.hailin.iot.remoting.processor;

import com.hailin.iot.remoting.InvokeContext;
import com.hailin.iot.remoting.RemotingContext;
import com.hailin.iot.remoting.RpcAsyncContext;
import com.hailin.iot.remoting.UserProcessor;
import com.hailin.iot.remoting.util.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
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
        long currentTimestamp = System.currentTimeMillis();
        if (isTimeoutCmd(ctx)) {
            timeoutLog(ctx, currentTimestamp);// do some log
            return;// then, discard this request
        }

        MqttFixedHeader fixedHeader = msg.fixedHeader();
        ctx.getInvokeContext().putIfAbsent(InvokeContext.IOT_PROCESS_WAIT_TIME,
                currentTimestamp - ctx.getArriveTimestamp());

        preProcessRemotingContext(ctx, msg, currentTimestamp);


        // 是否要处理 UserProcessor
        if (Objects.isNull(ctx.getUserProcessor(msg.fixedHeader().messageType()))) {
            return;
        }
        UserProcessor userProcessor = ctx.getUserProcessor(fixedHeader.messageType());

        // to check whether get executor using executor selector
        if (Objects.isNull( userProcessor.getExecutor())) {
            dispatchToUserProcessor(ctx , msg);
        } else {
            //try get executor with strategy
            userProcessor.getExecutor().execute(() -> dispatchToUserProcessor(ctx , msg));
        }

    }


    /**
     * print some log when request timeout and discarded in io thread.
     */
    private void timeoutLog(final RemotingContext ctx, long currentTimestamp) {
        if (logger.isDebugEnabled()) {
            logger
                    .debug(
                            "request id [{}] currenTimestamp [{}] - arriveTime [{}] = server cost [{}] >= timeout value [{}].",
                            ctx.getId(), currentTimestamp, ctx.getArriveTimestamp(),
                            (currentTimestamp - ctx.getArriveTimestamp()), getTimeout());
        }

        String remoteAddr = "UNKNOWN";
        if (null != ctx) {
            ChannelHandlerContext channelCtx = ctx.getChannelContext();
            Channel channel = channelCtx.channel();
            if (null != channel) {
                remoteAddr = RemotingUtil.parseRemoteAddress(channel);
            }
        }
        logger
                .warn(
                        "Rpc request id[{}], from remoteAddr[{}] stop process, total wait time in queue is [{}], client timeout setting is [{}].",
                        ctx.getId(), remoteAddr, (currentTimestamp - ctx.getArriveTimestamp()), getTimeout());
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

    /**
     * 获取超时时间
     */
    protected int getTimeout(){
        return 0;
    }

    protected boolean isTimeoutDiscard() {
        return false;
    }

    private boolean isTimeoutCmd(RemotingContext ctx) {
        if ( isTimeoutDiscard()
                && this.getTimeout() > 0 && (System.currentTimeMillis() - ctx.getArriveTimestamp()) > this.getTimeout()) {
            return true;
        }
        return false;
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
