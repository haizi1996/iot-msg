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

    default byte[] storeMessage(HbaseTemplate hbaseTemplate , String roamTableName , Message message){

        String sessionId = HbaseUtils.buildSession(message);
        byte[] rowKey = HbaseUtils.buildRowKey( sessionId, message.getMessageId());
        return hbaseTemplate.execute(roamTableName ,htable -> {
            Put put = new Put(rowKey)
                    .addColumn(familyName.getBytes(), SESSION_COLUMN.getBytes(), sessionId.getBytes())
                    .addColumn(familyName.getBytes(), SEND_USER_COLUMN.getBytes(), message.getSendUser().getBytes())
                    .addColumn(familyName.getBytes(), ACCEPT_USER_COLUMN.getBytes(), message.getAcceptUser().getBytes())
                    .addColumn(familyName.getBytes() , CONTENT_COLUMN.getBytes() , MessageUtil.serializeToByteArray(message));
            htable.put(put);
            return rowKey;} );
    }

    List<Message> getMessageByRowKeys(byte[] rowKeys , Integer limit);
}
