package com.hailin.iot.common.remoting.processor;

import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.remoting.InvokeContext;
import com.hailin.iot.common.remoting.RemotingContext;
import com.hailin.iot.common.remoting.UserProcessor;
import com.hailin.iot.common.util.MessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

public class MqttPublishProcessor extends AbstractRemotingProcessor<MqttPublishMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttPublishProcessor.class);

    private static final int TIMEOUT = 500;

    @Override
    public void doProcess(RemotingContext ctx, MqttPublishMessage msg) throws Exception {

        long currentTimestamp = System.currentTimeMillis();

        Message message = MessageUtil.deSerializationToObj(msg.content().array());

        message.setSendTime(currentTimestamp );

        preProcessRemotingContext(ctx, message, currentTimestamp);
        if (ctx.isTimeoutDiscard() && ctx.isRequestTimeout()) {
            timeoutLog(cmd, currentTimestamp, ctx);// do some log
            return;// then, discard this request
        }
        debugLog(ctx,  currentTimestamp);
        // decode request all

        dispatchToUserProcessor(ctx, msg);

    }

    @Override
    public void process(RemotingContext ctx, MqttPublishMessage msg, ExecutorService defaultExecutor) throws Exception {
        MqttFixedHeader fixedHeader = msg.fixedHeader();
        UserProcessor userProcessor = ctx.getUserProcessor(fixedHeader.messageType());
        if (userProcessor == null) {
            String errMsg = "No user processor found for request: " + fixedHeader.messageType();
            LOGGER.error(errMsg);
            return;// must end process
        }

        // set timeout check state from user's processor
        ctx.setTimeoutDiscard(userProcessor.timeoutDiscard());

        // to check whether to process in io thread
        if (userProcessor.processInIOThread()) {

            // process in io thread
            new ProcessTask(ctx, msg).run();
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
            executor = (this.getExecutor() == null ? defaultExecutor : this.getExecutor());
        }

        // use the final executor dispatch process task
        executor.execute(new ProcessTask(ctx, msg));
    }

    private void preProcessRemotingContext(RemotingContext ctx, Message msg,
                                           long currentTimestamp) {
        ctx.setArriveTimestamp(msg.getSendTime());
        ctx.setTimeout(TIMEOUT);
        ctx.getInvokeContext().putIfAbsent(InvokeContext.IOT_PROCESS_WAIT_TIME,
                currentTimestamp - msg.getSendTime());
    }

    /**
     * dispatch request command to user processor
     * @param ctx remoting context
     * @param msg rpc request command
     */
    private void dispatchToUserProcessor(RemotingContext ctx, MqttPublishMessage msg) {
        final int id = msg.getId();
        final byte type = msg.getType();
        // processor here must not be null, for it have been checked before
        UserProcessor processor = ctx.getUserProcessor(msg.getRequestClass());
        if (processor instanceof AsyncUserProcessor) {
            try {
                processor.handleRequest(processor.preHandleRequest(ctx, msg.getRequestObject()),
                        new RpcAsyncContext(ctx, msg, this), msg.getRequestObject());
            } catch (RejectedExecutionException e) {
                logger
                        .warn("RejectedExecutionException occurred when do ASYNC process in RpcRequestProcessor");
                sendResponseIfNecessary(ctx, type, this.getCommandFactory()
                        .createExceptionResponse(id, ResponseStatus.SERVER_THREADPOOL_BUSY));
            } catch (Throwable t) {
                String errMsg = "AYSNC process rpc request failed in RpcRequestProcessor, id=" + id;
                logger.error(errMsg, t);
                sendResponseIfNecessary(ctx, type, this.getCommandFactory()
                        .createExceptionResponse(id, t, errMsg));
            }
        } else {
            try {
                Object responseObject = processor
                        .handleRequest(processor.preHandleRequest(ctx, msg.getRequestObject()),
                                msg.getRequestObject());

                sendResponseIfNecessary(ctx, type,
                        this.getCommandFactory().createResponse(responseObject, msg));
            } catch (RejectedExecutionException e) {
                logger
                        .warn("RejectedExecutionException occurred when do SYNC process in RpcRequestProcessor");
                sendResponseIfNecessary(ctx, type, this.getCommandFactory()
                        .createExceptionResponse(id, ResponseStatus.SERVER_THREADPOOL_BUSY));
            } catch (Throwable t) {
                String errMsg = "SYNC process rpc request failed in RpcRequestProcessor, id=" + id;
                logger.error(errMsg, t);
                sendResponseIfNecessary(ctx, type, this.getCommandFactory()
                        .createExceptionResponse(id, t, errMsg));
            }
        }
    }
}
