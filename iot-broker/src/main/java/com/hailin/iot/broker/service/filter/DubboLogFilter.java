package com.hailin.iot.broker.service.filter;

import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dubbo rpc
 * 日志
 * @author zhanghailin
 */

public class DubboLogFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DubboLogFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        LOGGER.debug("invoke in ....");
        return invoker.invoke(invocation);
    }
}
