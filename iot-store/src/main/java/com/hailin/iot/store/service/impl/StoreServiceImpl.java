package com.hailin.iot.store.service.impl;

import com.hailin.iot.common.model.Message;
import com.hailin.iot.store.service.StoreService;
import com.hailin.iot.store.timeline.TimeLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private TimeLine redisTimeLine;

    @Override
    public void storeMessage(Message message) {





    }
}
