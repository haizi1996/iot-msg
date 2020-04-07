package com.hailin.iot.store.service.impl;

import com.google.common.collect.Lists;
import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.util.MessageUtil;
import com.hailin.iot.store.hbase.HbaseUtils;
import com.hailin.iot.store.service.StoreService;
import com.hailin.iot.store.timeline.TimeLine;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Service;

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

        String seesionId = message.getAcceptUser();
        byte[] seesionRowkey = HbaseUtils.buildRowKeyDesc(seesionId ,  message.getMessageId());

        hbaseTemplate.execute(roamTableName ,htable -> {
            Put sessionPut = new Put(seesionRowkey)
                    .addColumn(familyName.getBytes(), SESSION_COLUMN.getBytes(), seesionId.getBytes())
                    .addColumn(familyName.getBytes(), SEND_USER_COLUMN.getBytes(), message.getSendUser().getBytes())
                    .addColumn(familyName.getBytes(), ACCEPT_USER_COLUMN.getBytes(), message.getAcceptUser().getBytes())
                    .addColumn(familyName.getBytes() , CONTENT_COLUMN.getBytes() , MessageUtil.serializeToByteArray(message));
            htable.put(sessionPut);
            return seesionRowkey;} );
    }

    @Override
    public void storePrivateChatMessage(Message message) {
        // 存储发送人的timeline
//        String sendUserTimelineId = HbaseUtils.buildSessionId(message.getSendUser() , message.getAcceptUser());
        byte[] sendUserRowKey = HbaseUtils.buildRowKeyAsc( message.getSendUser(), message.getMessageId());

        // 存储接收人的timeline
//        String acceptUserTimelineId = HbaseUtils.buildSessionId(message.getSendUser() , message.getAcceptUser());
        byte[] acceptUserRowKey = HbaseUtils.buildRowKeyAsc( message.getAcceptUser(), message.getMessageId());

        String seesionId = HbaseUtils.buildSession(message);
        byte[] seesionRowkey = HbaseUtils.buildRowKeyDesc(seesionId ,  message.getMessageId());

        hbaseTemplate.execute(roamTableName ,htable -> {
            Put sendUserPut = new Put(sendUserRowKey)
                    .addColumn(familyName.getBytes(), SESSION_COLUMN.getBytes(), seesionId.getBytes())
                    .addColumn(familyName.getBytes(), SEND_USER_COLUMN.getBytes(), message.getSendUser().getBytes())
                    .addColumn(familyName.getBytes(), ACCEPT_USER_COLUMN.getBytes(), message.getAcceptUser().getBytes())
                    .addColumn(familyName.getBytes() , CONTENT_COLUMN.getBytes() , MessageUtil.serializeToByteArray(message));
            Put acceptUserPut = new Put(acceptUserRowKey)
                    .addColumn(familyName.getBytes(), SESSION_COLUMN.getBytes(), seesionId.getBytes())
                    .addColumn(familyName.getBytes(), SEND_USER_COLUMN.getBytes(), message.getSendUser().getBytes())
                    .addColumn(familyName.getBytes(), ACCEPT_USER_COLUMN.getBytes(), message.getAcceptUser().getBytes())
                    .addColumn(familyName.getBytes() , CONTENT_COLUMN.getBytes() , MessageUtil.serializeToByteArray(message));
            Put sessionPut = new Put(seesionRowkey)
                    .addColumn(familyName.getBytes(), SESSION_COLUMN.getBytes(), seesionId.getBytes())
                    .addColumn(familyName.getBytes(), SEND_USER_COLUMN.getBytes(), message.getSendUser().getBytes())
                    .addColumn(familyName.getBytes(), ACCEPT_USER_COLUMN.getBytes(), message.getAcceptUser().getBytes())
                    .addColumn(familyName.getBytes() , CONTENT_COLUMN.getBytes() , MessageUtil.serializeToByteArray(message));
            htable.put(Lists.newArrayList(sendUserPut , acceptUserPut , sessionPut));
            return sendUserRowKey;} );
    }

    @Override
    public List<Message> getMessageByRowKey(byte[] startRowKey, boolean inclusive , Integer limit) {

        Scan scan = new Scan().withStartRow(startRowKey , false).setLimit(limit);
        return hbaseTemplate.find(roamTableName , scan ,(result ,i)-> MessageUtil.deSerializationToObj(result.getValue(familyName.getBytes() , CONTENT_COLUMN.getBytes())));
    }
}
