package com.hailin.iot.common.contanst;

import lombok.AllArgsConstructor;

/**
 * 消息的枚举位
 * @author zhanghailin
 */
@AllArgsConstructor
public enum MessageBit {

    //群聊
    GROUP_CHAT(1L << 0),
    //私聊
    PRIVATE_CHAT(1 << 1);

    private long bit;

    public boolean isBit(long bit){
        return (this.bit & bit) == bit;
    }


}
