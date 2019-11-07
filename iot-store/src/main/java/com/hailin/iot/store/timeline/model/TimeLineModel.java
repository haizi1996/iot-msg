package com.hailin.iot.store.timeline.model;

/**
 * timeline模型的接口
 * @author zhanghailin
 */
public interface TimeLineModel {

    /**
     *
     * @return 获取 key模型实体的key
     */
    byte[] getKey();

    /**
     *
     * @return 获取timeline的id
     */
    byte[] getTimeLineId();

}
