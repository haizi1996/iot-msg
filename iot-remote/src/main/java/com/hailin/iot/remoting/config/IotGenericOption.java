package com.hailin.iot.remoting.config;

import com.hailin.iot.remoting.ConnectionSelectStrategy;

public class IotGenericOption<T> extends IotOption<T> {

    /*------------ NETTY Config Start ------------*/
    public static final IotOption<Boolean> TCP_NODELAY = valueOf("iot.tcp.nodelay",true);
    public static final IotOption<Boolean> TCP_SO_REUSEADDR = valueOf("iot.tcp.so.reuseaddr",true);
    public static final IotOption<Boolean> TCP_SO_KEEPALIVE = valueOf("iot.tcp.so.keepalive",true);

    public static final IotOption<Integer> NETTY_IO_RATIO = valueOf("iot.netty.io.ratio",70);
    public static final IotOption<Boolean> NETTY_BUFFER_POOLED = valueOf("iot.netty.buffer.pooled",true);

    public static final IotOption<Integer> NETTY_BUFFER_HIGH_WATER_MARK = valueOf("iot.netty.buffer.high.watermark",64 * 1024);
    public static final IotOption<Integer> NETTY_BUFFER_LOW_WATER_MARK  = valueOf("iot.netty.buffer.low.watermark",32 * 1024);

    public static final IotOption<Boolean> NETTY_EPOLL_SWITCH = valueOf("iot.netty.epoll.switch",true);

    public static final IotOption<Boolean> TCP_IDLE_SWITCH = valueOf("iot.tcp.heartbeat.switch",true);
    /*------------ NETTY Config End ------------*/

    /*------------ Thread Pool Config Start ------------*/
    public static final IotOption<Integer> TP_MIN_SIZE = valueOf("iot.tp.min",20);
    public static final IotOption<Integer> TP_MAX_SIZE = valueOf("iot.tp.max",400);
    public static final IotOption<Integer> TP_QUEUE_SIZE = valueOf("iot.tp.queue",600);
    public static final IotOption<Integer> TP_KEEPALIVE_TIME = valueOf("iot.tp.keepalive",60);
    
    public static final IotOption<ConnectionSelectStrategy> CONNECTION_SELECT_STRATEGY   = valueOf("CONNECTION_SELECT_STRATEGY");

    public IotGenericOption(String name, T defaultValue) {
        super(name, defaultValue);
    }
}
