package com.hailin.iot.common.remoting;

/**
 * 异步上下文接口
 * @author hailin
 */
public interface AsyncContext {

    /**
     * 发送请求回调
     * @param obj 请求的结果集
     */
    void sendResponse(Object obj);

}
