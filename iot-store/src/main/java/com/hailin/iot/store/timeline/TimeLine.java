package com.hailin.iot.store.timeline;

import com.hailin.iot.store.timeline.model.TimeLineModel;

import java.util.List;

/**
 * timeline 模型的接口
 * @author zhanghailin
 */
public interface TimeLine {

    /**
     * 存储一个model到timelin队列里面
     * @param model timelineModel
     */
    boolean putModel( TimeLineModel model);

    boolean putModel(List<TimeLineModel> model);

    /**
     * 从对头获取获取 timemodel集合
     * @param limit 集合元素的个数
     */
    List<TimeLineModel> getModels(byte[] timeLineId , int limit);


    /**
     * 从对头移除多个model元素
     * @param limit 个数
     */
    boolean remove(byte[] timeLineId , int limit);

}
