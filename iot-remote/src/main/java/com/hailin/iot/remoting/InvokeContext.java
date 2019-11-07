package com.hailin.iot.remoting;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行的上下文
 * @author hailin
 */
public class InvokeContext {

    public static final String CLIENT_LOCAL_IP = "iot.client.local.ip";
    public static final String CLIENT_LOCAL_PORT = "iot.client.local.port";
    public static final String CLIENT_REMOT_IP = "iot.client.remote.ip";
    public static final String CLIENT_REMOT_PORT = "iot.client.remote.port";

    public final static String CLIENT_CONN_CREATETIME = "iot.client.conn.createtime";

    public final static String SERVER_LOCAL_IP = "iot.server.local.ip";
    public final static String SERVER_LOCAL_PORT = "iot.server.local.port";
    public final static String SERVER_REMOTE_IP = "iot.server.remote.ip";
    public final static String SERVER_REMOTE_PORT = "iot.server.remote.port";

    public final static String IOT_INVOKE_REQUEST_ID = "iot.invoke.request.id";
    public final static String IOT_PROCESS_WAIT_TIME = "iot.invoke.wait.time";
    public final static String IOT_CUSTOM_SERIALIZER = "iot.invoke.custom.serializer";
    public final static String IOT_CRC_SWITCH = "iot.invoke.crc.switch";
    public final static int INITIAL_SIZE = 8;

    private ConcurrentHashMap<String , Object> context;

    public InvokeContext() {
        this.context = new ConcurrentHashMap<>();
    }

    public void putIfAbsent(String key , Object value){
        this.context.putIfAbsent(key, value);
    }

    public void put(String key, Object value) {
        this.context.put(key, value);
    }

    public <T> T get(String key) {
        return (T) this.context.get(key);
    }

    public <T> T get(String key, T defaultIfNotFound) {
        return this.context.get(key) != null ? (T) this.context.get(key) : defaultIfNotFound;
    }

    public void clear() {
        this.context.clear();
    }
}
