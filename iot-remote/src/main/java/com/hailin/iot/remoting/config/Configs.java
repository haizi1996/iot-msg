package com.hailin.iot.remoting.config;

import com.hailin.iot.remoting.serialization.SerializerManager;

/**
 * 定义一些配置常量
 * @author hailin
 */
public interface Configs {
    /** TCP_NODELAY option */
    public static final String TCP_NODELAY                           = "iot.tcp.nodelay";
    public static final String TCP_NODELAY_DEFAULT                   = "true";

    /** TCP SO_REUSEADDR option */
    public static final String TCP_SO_REUSEADDR                      = "iot.tcp.so.reuseaddr";
    public static final String TCP_SO_REUSEADDR_DEFAULT              = "true";

    /** TCP SO_BACKLOG option */
    public static final String TCP_SO_BACKLOG                        = "iot.tcp.so.backlog";
    public static final String TCP_SO_BACKLOG_DEFAULT                = "1024";

    /** TCP SO_KEEPALIVE option */
    public static final String TCP_SO_KEEPALIVE                      = "iot.tcp.so.keepalive";
    public static final String TCP_SO_KEEPALIVE_DEFAULT              = "true";

    /** Netty ioRatio option*/
    public static final String NETTY_IO_RATIO                        = "iot.netty.io.ratio";
    public static final String NETTY_IO_RATIO_DEFAULT                = "70";

    /** Netty buffer allocator, enabled as default */
    public static final String NETTY_BUFFER_POOLED                   = "iot.netty.buffer.pooled";
    public static final String NETTY_BUFFER_POOLED_DEFAULT           = "true";

    /** Netty buffer high watermark */
    public static final String NETTY_BUFFER_HIGH_WATERMARK           = "iot.netty.buffer.high.watermark";
    public static final String NETTY_BUFFER_HIGH_WATERMARK_DEFAULT   = Integer.toString(64 * 1024);

    /** Netty buffer low watermark */
    public static final String NETTY_BUFFER_LOW_WATERMARK            = "iot.netty.buffer.low.watermark";
    public static final String NETTY_BUFFER_LOW_WATERMARK_DEFAULT    = Integer.toString(32 * 1024);

    /** Netty epoll switch */
    public static final String NETTY_EPOLL_SWITCH                    = "iot.netty.epoll.switch";
    public static final String NETTY_EPOLL_SWITCH_DEFAULT            = "true";

    /** Netty epoll level trigger enabled */
    public static final String NETTY_EPOLL_LT                        = "iot.netty.epoll.lt";
    public static final String NETTY_EPOLL_LT_DEFAULT                = "true";

    // ~~~ configs and default values for idle

    /** TCP idle switch */
    public static final String TCP_IDLE_SWITCH                       = "iot.tcp.heartbeat.switch";
    public static final String TCP_IDLE_SWITCH_DEFAULT               = "false";

    /** TCP idle interval for client */
    public static final String TCP_IDLE                              = "iot.tcp.heartbeat.interval";
    public static final String TCP_IDLE_DEFAULT                      = "15000";

    /** TCP idle triggered max times if no response*/
    public static final String TCP_IDLE_MAXTIMES                     = "iot.tcp.heartbeat.maxtimes";
    public static final String TCP_IDLE_MAXTIMES_DEFAULT             = "3";

    /** TCP idle interval for server */
    public static final String TCP_SERVER_IDLE                       = "iot.tcp.server.idle.interval";
    public static final String TCP_SERVER_IDLE_DEFAULT               = "90000";

    // ~~~ configs and default values for connection manager

    /** Thread pool min size for the connection manager executor */
    public static final String CONN_CREATE_TP_MIN_SIZE               = "iot.conn.create.tp.min";
    public static final String CONN_CREATE_TP_MIN_SIZE_DEFAULT       = "3";

    /** Thread pool max size for the connection manager executor */
    public static final String CONN_CREATE_TP_MAX_SIZE               = "iot.conn.create.tp.max";
    public static final String CONN_CREATE_TP_MAX_SIZE_DEFAULT       = "8";

    /** Thread pool queue size for the connection manager executor */
    public static final String CONN_CREATE_TP_QUEUE_SIZE             = "iot.conn.create.tp.queue";
    public static final String CONN_CREATE_TP_QUEUE_SIZE_DEFAULT     = "50";

    /** Thread pool keep alive time for the connection manager executor */
    public static final String CONN_CREATE_TP_KEEPALIVE_TIME         = "iot.conn.create.tp.keepalive";
    public static final String CONN_CREATE_TP_KEEPALIVE_TIME_DEFAULT = "60";

    /** Default connect timeout value, time unit: ms  */
    public static final int    DEFAULT_CONNECT_TIMEOUT               = 1000;

    /** default connection number per url */
    public static final int    DEFAULT_CONN_NUM_PER_URL              = 1;

    /** max connection number of each url */
    public static final int    MAX_CONN_NUM_PER_URL                  = 100 * 10000;

    // ~~~ configs for processor manager

    /**
     * Thread pool min size for the default executor.
     */
    public static final String TP_MIN_SIZE                           = "iot.tp.min";
    public static final String TP_MIN_SIZE_DEFAULT                   = "20";

    /**
     * Thread pool max size for the default executor.
     */
    public static final String TP_MAX_SIZE                           = "iot.tp.max";
    public static final String TP_MAX_SIZE_DEFAULT                   = "400";

    /**
     * Thread pool queue size for the default executor.
     */
    public static final String TP_QUEUE_SIZE                         = "iot.tp.queue";
    public static final String TP_QUEUE_SIZE_DEFAULT                 = "600";

    /**
     * Thread pool keep alive time for the default executor
     */
    public static final String TP_KEEPALIVE_TIME                     = "iot.tp.keepalive";
    public static final String TP_KEEPALIVE_TIME_DEFAULT             = "60";

    // ~~~ configs and default values for reconnect manager

    /** Reconnection switch */
    public static final String CONN_RECONNECT_SWITCH                 = "iot.conn.reconnect.switch";
    public static final String CONN_RECONNECT_SWITCH_DEFAULT         = "false";

    // ~~~ configs and default values for connection monitor


    public static final String CONN_MONITOR_SWITCH                   = "iot.conn.monitor.switch";
    public static final String CONN_MONITOR_SWITCH_DEFAULT           = "false";

    /** Initial delay to execute schedule task for connection monitor */
    public static final String CONN_MONITOR_INITIAL_DELAY            = "iot.conn.monitor.initial.delay";
    public static final String CONN_MONITOR_INITIAL_DELAY_DEFAULT    = "10000";

    /** Period of schedule task for connection monitor */
    public static final String CONN_MONITOR_PERIOD                   = "iot.conn.monitor.period";
    public static final String CONN_MONITOR_PERIOD_DEFAULT           = "180000";

    /** Connection threshold */
    public static final String CONN_THRESHOLD                        = "iot.conn.threshold";
    public static final String CONN_THRESHOLD_DEFAULT                = "3";

    /** Retry detect period for ScheduledDisconnectStrategy */
    @Deprecated
    public static final String RETRY_DETECT_PERIOD                   = "iot.retry.delete.period";
    public static final String RETRY_DETECT_PERIOD_DEFAULT           = "5000";

    /** Connection status */
    public static final String CONN_SERVICE_STATUS                   = "iot.conn.service.status";
    public static final String CONN_SERVICE_STATUS_OFF               = "off";
    public static final String CONN_SERVICE_STATUS_ON                = "on";

    // ~~~ configs and default values for serializer

    /** Codec type */
    public static final String SERIALIZER                            = "iot.serializer";
    public static final String SERIALIZER_DEFAULT                    = String
            .valueOf(SerializerManager.Hessian2);

    /** Charset */
    public static final String DEFAULT_CHARSET                       = "UTF-8";
}
