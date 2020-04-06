package com.hailin.iot.store.service;

import com.hailin.iot.common.model.Message;
import com.hailin.iot.common.util.MessageUtil;
import com.hailin.iot.store.hbase.HbaseUtils;
import org.apache.hadoop.hbase.client.Put;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.util.Collection;
import java.util.List;

/**
 * 封装对外提供的接口
 * @author zhanghailin
 */
public interface StoreService {
    //列族
    String familyName = "message";

    String SESSION_COLUMN = "session_Id";
    String SEND_USER_COLUMN = "send_user";
    String ACCEPT_USER_COLUMN = "accept_User";
    String CONTENT_COLUMN = "content";


    /**
     * 存儲群聊消息
     * @param message
     */
    void storeGroupChatMessage(Message message);
    /**
     * 存儲私聊消息
     * @param message
     */
    void storePrivateChatMessage(Message message);

    default void storeMessage(HbaseTemplate hbaseTemplate , String roamTableName , Message message){

        // 存储发送人的timeline
        String sendUserTimelineId = HbaseUtils.buildSessionId(message.getSendUser() , message.getAcceptUser());
        byte[] sendUserRowKey = HbaseUtils.buildRowKey( sendUserTimelineId, message.getMessageId());

        // 存储接收人的timeline
        String acceptUserTimelineId = HbaseUtils.buildSessionId(message.getSendUser() , message.getAcceptUser());
        byte[] acceptUserRowKey = HbaseUtils.buildRowKey( sendUserTimelineId, message.getMessageId());
        hbaseTemplate.execute(roamTableName ,htable -> {
            Put sendUserPut = new Put(sendUserRowKey)
                    .addColumn(familyName.getBytes(), SESSION_COLUMN.getBytes(), sendUserTimelineId.getBytes())
                    .addColumn(familyName.getBytes(), SEND_USER_COLUMN.getBytes(), message.getSendUser().getBytes())
                    .addColumn(familyName.getBytes(), ACCEPT_USER_COLUMN.getBytes(), message.getAcceptUser().getBytes())
                    .addColumn(familyName.getBytes() , CONTENT_COLUMN.getBytes() , MessageUtil.serializeToByteArray(message));

            htable.put(sendUserPut);
            Put acceptUserPut = new Put(acceptUserRowKey)
                    .addColumn(familyName.getBytes(), SESSION_COLUMN.getBytes(), acceptUserTimelineId.getBytes())
                    .addColumn(familyName.getBytes(), SEND_USER_COLUMN.getBytes(), message.getSendUser().getBytes())
                    .addColumn(familyName.getBytes(), ACCEPT_USER_COLUMN.getBytes(), message.getAcceptUser().getBytes())
                    .addColumn(familyName.getBytes() , CONTENT_COLUMN.getBytes() , MessageUtil.serializeToByteArray(message));
            htable.put(acceptUserPut);
            return sendUserRowKey;} );

    }

    List<Message> getMessageByRowKeys(byte[] rowKeys , Integer limit);
}
