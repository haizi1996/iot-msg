package com.hailin.iot.store.service;

import com.hailin.iot.common.model.Message;

/**
 * 封装对外提供的接口
 * @author zhanghailin
 */
public interface StoreService {


    void storeMessage(Message message);
}
