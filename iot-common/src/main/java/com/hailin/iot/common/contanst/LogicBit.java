package com.hailin.iot.common.contanst;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LogicBit {

    //消息是否在推送
    IS_PUSH(1L << 0),
    //是否在线
    IS_ONLINE(1L << 1);

    private long bit;

    public long addBit(long logic) {
        return this.bit | logic;
    }

    public boolean isBit(long bit){
        return (this.bit & bit) == bit;
    }
    
    


}
