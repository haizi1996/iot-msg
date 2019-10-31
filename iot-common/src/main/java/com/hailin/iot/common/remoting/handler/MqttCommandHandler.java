package com.hailin.iot.common.remoting.handler;

import com.hailin.iot.common.remoting.RemotingContext;
import com.hailin.iot.common.remoting.command.CommandFactory;
import com.hailin.iot.common.remoting.processor.AbstractRemotingProcessor;
import com.hailin.iot.common.remoting.processor.ProcessorManager;
import com.hailin.iot.common.remoting.processor.RemotingProcessor;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * Mqtt消息命令的处理器
 * @author hailin
 */
public class MqttCommandHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(MqttCommandHandler.class);
    /** All processors */
    private ProcessorManager processorManager;

    private CommandFactory commandFactory;

    public MqttCommandHandler() {
        this.commandFactory = commandFactory;
        this.processorManager = new ProcessorManager();
        //process request
//        this.processorManager.registerProcessor(MqttCommandCode.RPC_REQUEST,
//                new RpcRequestProcessor(this.commandFactory));
        //process response
//        this.processorManager.registerProcessor(MqttCommandCode.RPC_RESPONSE,
//                new RpcResponseProcessor());

//        this.processorManager.registerProcessor(CommonCommandCode.HEARTBEAT,
//                new MqttHeartBeatProcessor());

        this.processorManager
                .registerDefaultProcessor(new AbstractRemotingProcessor<MqttMessage>() {
                    @Override
                    public void doProcess(RemotingContext ctx, MqttMessage msg) throws Exception {
                        logger.error("No processor available for command code {}, msgId {}");
                    }
                });
    }

    @Override
    public void handleCommand(RemotingContext ctx, Object msg) {

    }

    @Override
    public void registerProcessor( RemotingProcessor<?> processor) {

    }

    @Override
    public void registerDefaultExecutor(ExecutorService executor) {

    }

    @Override
    public ExecutorService getDefaultExecutor() {
        return null;
    }
}
