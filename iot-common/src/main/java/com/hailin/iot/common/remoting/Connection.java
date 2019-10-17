package com.hailin.iot.common.remoting;

import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.Channel;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 一个抽象的socker channel
 * @author hailin
 */
public class Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);

    private Channel channel;

    private static final AttributeKey<Connection> CONNECTION = AttributeKey.valueOf("connection");

    public static final AttributeKey<Integer>  HEARTBEAT_COUNT  = AttributeKey.valueOf("heartbeatCount");

    public static final AttributeKey<Boolean>  HEARTBEAT_SWITCH = AttributeKey.valueOf("heartbeatSwitch");

    public static final AttributeKey<Byte>                                        VERSION          = AttributeKey
            .valueOf("version");
    private byte                                                                  version          = RpcProtocolV2.PROTOCOL_VERSION_1;

    private Url                                                                   url;

    private final ConcurrentHashMap<Integer/* id */, String/* poolKey */> id2PoolKey       = new ConcurrentHashMap<Integer, String>(
            256);

    private Set<String> poolKeys         = new ConcurrentHashSet<String>();

    private AtomicBoolean closed           = new AtomicBoolean(
            false);

    private final ConcurrentHashMap<String/* attr key*/, Object /*attr value*/> attributes       = new ConcurrentHashMap<String, Object>();

}
