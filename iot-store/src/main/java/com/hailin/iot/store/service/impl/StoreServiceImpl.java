package com.hailin.iot.store.service.impl;

import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.util.MessageUtil;
import com.hailin.iot.store.service.StoreService;
import com.hailin.iot.store.timeline.TimeLine;
import com.hailin.iot.store.timeline.model.RedisTimelineModel;
import org.apache.hadoop.hbase.client.Scan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {

    @Value("${spring.data.hbase.config.roamTable}")
    private String roamTableName;

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private TimeLine redisTimeLine;

    @Override
    public void storeGroupChatMessage(Message message) {
        storeMessage(hbaseTemplate , roamTableName , message);
    }

    @Override
    public void storePrivateChatMessage(Message message) {
        //消息存储hbase 返回rowkey
        storeMessage(hbaseTemplate , roamTableName , message);
        //构建timeline模型， 去也是hbase的二级索引
//        redisTimeLine.putModel(RedisTimelineModel.builder().timeLineId(message.getAcceptUser().getBytes()).key(rowKey).build());

    }

    @Override
    public List<Message> getMessageByRowKeys(byte[] startRowKeys , Integer limit) {

        Scan scan = new Scan().withStartRow(startRowKeys , true).setLimit(limit);
        return hbaseTemplate.find(roamTableName , scan ,(result ,i)-> MessageUtil.deSerializationToObj(result.getValue(familyName.getBytes() , CONTENT_COLUMN.getBytes())));
    }
}
