package com.hailin.iot.store.service.impl;

import com.hailin.iot.common.model.Message;
import com.hailin.iot.store.service.StoreService;
import com.hailin.iot.store.timeline.TimeLine;
import com.hailin.iot.store.timeline.model.RedisTimelineModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl implements StoreService {

    @Value("${spring.data.hbase.roamTable}")
    private String roamTableName;

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private TimeLine redisTimeLine;

    @Override
    public void storeGroupChatMessage(Message message) {
        byte[] rowKey = storeMessage(hbaseTemplate , roamTableName , message);
    }

    @Override
    public void storePrivateChatMessage(Message message) {
        //消息存储hbase 返回rowkey
        byte[] rowKey = storeMessage(hbaseTemplate , roamTableName , message);
        //构建timeline模型， 去也是hbase的二级索引
        redisTimeLine.putModel(RedisTimelineModel.builder().timeLineId(message.getAcceptUser().getBytes()).key(rowKey).build());

    }
}
