package com.hailin.iot.remoting;

import com.hailin.iot.remoting.connection.Connection;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接的事件监听器
 * @author hailin
 */
public class ConnectionEventListener {



    private ConcurrentHashMap<ConnectionEventType , List<ConnectionEventProcessor>> processors = new ConcurrentHashMap<>(3);

    public void onEvent(ConnectionEventType type , String remoteAddress , Connection connection){
        List<ConnectionEventProcessor> processorList = this.processors.get(type);
        if(CollectionUtils.isEmpty(processorList)){
            return;
        }
        processorList.forEach(processor->processor.onEvent(remoteAddress , connection));
    }

    /**
     * 给特定的连接类型增加一个处理器
     * @param type 连接类型
     * @param processor 处理器
     */
    public void addConnectionEventProcessor(ConnectionEventType type,
                                            ConnectionEventProcessor processor) {
        List<ConnectionEventProcessor> processorList = this.processors.get(type);
        if (processorList == null) {
            this.processors.putIfAbsent(type, new ArrayList<ConnectionEventProcessor>(1));
            processorList = this.processors.get(type);
        }
        processorList.add(processor);
    }
}
