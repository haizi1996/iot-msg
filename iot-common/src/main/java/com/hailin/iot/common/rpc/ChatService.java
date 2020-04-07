package com.hailin.iot.common.rpc;


/**
 * 聊天服务提供接口
 * RPC 接口
 * @author hailin
 */
public interface ChatService {

    /**
     * 通知私聊的接口
     * @return
     */
    boolean noticePrivateChat(String acceptUsername , Long messageId );

    /**
     * 通知群聊接口
     * @return
     */
    boolean noticeGroupChat();
}
