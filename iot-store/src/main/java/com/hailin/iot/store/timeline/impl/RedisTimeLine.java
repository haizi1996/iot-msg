package com.hailin.iot.store.timeline.impl;

import com.hailin.iot.store.timeline.TimeLine;
import com.hailin.iot.store.timeline.model.TimeLineModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * redis 实现timeline模型
 * 右进左出的队列
 * @author zhanghailin
 */
@Service
public class RedisTimeLine implements TimeLine {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean putModel( TimeLineModel model) {
        return redisTemplate.opsForList().rightPush(model.getTimeLineId() , model.getKey()) > 0;
    }

    @Override
    public boolean putModel(List<TimeLineModel> model) {
        if(CollectionUtils.isEmpty(model)){
            return false;
        }
        byte[] timelineId = model.get(0).getTimeLineId();
        return redisTemplate.opsForList().rightPushAll(timelineId , model.stream().map(TimeLineModel::getKey).collect(Collectors.toList())) > 0;
    }

    @Override
    public List<TimeLineModel> getModels(byte[] timeLineId , int limit) {
        return redisTemplate.opsForList().range(timeLineId , 0 , limit - 1);
    }

    @Override
    public boolean remove(byte[] timeLineId , int limit) {
        int len = redisTemplate.opsForList().size(timeLineId).intValue();
        if(limit >= len){
            return redisTemplate.delete(timeLineId);
        }else {
            redisTemplate.opsForList().trim(timeLineId , limit,  - 1);
        }
        return true;
    }
}
