package com.hailin.iot.common.remoting.config.configs;

public class RpcConfigs {

    /**
     * Connection timeout key in url.
     */
    public static final String CONNECT_TIMEOUT_KEY = "_CONNECTTIMEOUT";

    /**
     * Connection number key of each address
     */
    public static final String CONNECTION_NUM_KEY = "_CONNECTIONNUM";

    /**
     * whether need to warm up connections
     */
    public static final String CONNECTION_WARMUP_KEY = "_CONNECTIONWARMUP";


    public static final String DISPATCH_MSG_LIST_IN_DEFAULT_EXECUTOR = "dispatch-msg-list-in-default-executor";
    public static final String DISPATCH_MSG_LIST_IN_DEFAULT_EXECUTOR_DEFAULT = "true";

}
