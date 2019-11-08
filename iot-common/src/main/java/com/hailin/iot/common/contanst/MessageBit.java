package com.hailin.iot.common.contanst;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息的枚举位
 * @author zhanghailin
 */
@AllArgsConstructor
@Getter
public enum MessageBit {

    //群聊
    GROUP_CHAT(1 << 0),
    //私聊
    PRIVATE_CHAT(1 << 1);

    private int bit;

    public boolean isBit(long bit){
        return (this.bit & bit) == bit;
    }


}
