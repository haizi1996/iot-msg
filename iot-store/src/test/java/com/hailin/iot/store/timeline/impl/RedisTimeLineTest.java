package com.hailin.iot.store.timeline.impl;

import com.hailin.iot.store.timeline.TimeLine;
import com.hailin.iot.store.timeline.model.RedisTimelineModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTimeLineTest {

    @Autowired
    private TimeLine redisTimeLine;

    @Test
    public void putModel() {

        RedisTimelineModel model = RedisTimelineModel.builder().key("row1".getBytes()).timeLineId( "key".getBytes()).build();

        redisTimeLine.putModel(model);
    }

    @Test
    public void putModel1() {
    }

    @Test
    public void getModels() {
        System.out.println(redisTimeLine.getModels( "key".getBytes(), 2 ).size());
    }

    @Test
    public void remove() {
    }
}