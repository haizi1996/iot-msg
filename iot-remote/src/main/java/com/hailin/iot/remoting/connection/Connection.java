package com.hailin.iot.remoting.connection;

import com.google.common.collect.Sets;
import com.hailin.iot.remoting.ConnectionManager;
import com.hailin.iot.remoting.HeartbeatTrigger;
import com.hailin.iot.remoting.MqttHeartbeatTrigger;
import com.hailin.iot.remoting.RemotingAddressParser;
import com.hailin.iot.remoting.future.InvokeFuture;
import com.hailin.iot.remoting.Url;
import com.hailin.iot.remoting.util.ConcurrentHashSet;
import com.hailin.iot.remoting.util.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 一个抽象的socker channel
 * @author hailin
 */
@Getter
@Setter
public class Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);

    @Getter
    private Channel channel;

    @Getter
    @Setter
    private ConnectionPool pool;


    public static final AttributeKey<Connection> CONNECTION = AttributeKey.valueOf("connection");

    //mqtt  连接确认
    public static final AttributeKey<Boolean> CONNECTION_ACK = AttributeKey.valueOf("mqtt_connection");

    public static final AttributeKey<Integer>  HEARTBEAT_COUNT  = AttributeKey.valueOf("heartbeatCount");

    public static final AttributeKey<Boolean>  HEARTBEAT_SWITCH = AttributeKey.valueOf("heartbeatSwitch");

    public static final AttributeKey<HeartbeatTrigger>  HEARTBEAT_TRIGGER = AttributeKey.valueOf("heartbeatTrigger");

    //初始化的消息ID
    public static final AttributeKey<AtomicLong>  MESSAGE_ID = AttributeKey.valueOf("messageId");

    //客户端标识
    public static final AttributeKey<String> CLIENT_IDENTIFIER = AttributeKey.valueOf("clientIdentifier");
    public static final AttributeKey<RemotingAddressParser> ADDRESS_PARSER = AttributeKey.valueOf("addressParser");

    @Getter
    @Setter
    private String userName;

    //心跳的futurn
    @Setter
    @Getter
    private InvokeFuture heartbeatFuture;

    @Getter
    private Url url;

    private Connection.TermType type;

    private final ConcurrentHashMap<Object/* id */, String/* poolKey */> id2PoolKey = new ConcurrentHashMap<Object, String>(
            256);

    private Set<String> poolKeys = new ConcurrentHashSet<String>();

    private AtomicBoolean closed = new AtomicBoolean(false);

    private final ConcurrentHashMap<String/* attr key*/, Object /*attr value*/> attributes = new ConcurrentHashMap<String, Object>();

    private  final AtomicInteger referenceCount = new AtomicInteger();

    //连接管理器
//    @Getter
//    private final ConnectionManager connectionManager;

    private static final int NO_REFERENCE = 0;

    public Connection(Channel channel ) {
        this.channel = channel;
        this.channel.attr(CONNECTION).set(this);
//        this.connectionManager = connectionManager;
        this.init();
    }

    public Connection(Channel channel, Url url) {
        this(channel );
        this.url = url;
        this.poolKeys.add(url.getUniqueKey());
        this.init();
    }


    private void init() {
        this.channel.attr(HEARTBEAT_COUNT).set(0);
        this.channel.attr(HEARTBEAT_SWITCH).set(true);
        this.channel.attr(HEARTBEAT_TRIGGER).set(new MqttHeartbeatTrigger());
    }

    /**
     * 检查连接是否连通的
     */
    public boolean isFine() {
        return this.channel != null && this.channel.isActive();
    }

    /**
     * 连接引用加一
     */
    public void increaseRef() {
        this.referenceCount.getAndIncrement();
    }

    /**
     * 连接引用减一
     */
    public void decreaseRef() {
        this.referenceCount.getAndDecrement();
    }

    /**
     * 检查连接是否无引用
     */
    public boolean noRef(){
        return  this.referenceCount.get() == NO_REFERENCE;
    }

    /**
     * 获取远程Socket
     */
    public InetSocketAddress getRemoteAddress(){
        return (InetSocketAddress)this.channel.remoteAddress();
    }

    /**
     * 获取远程的IP地址
     */
    public String getRemoteIp(){
        return RemotingUtil.parseRemoteIP(this.channel);
    }
    public int getRemotePort() {
        return RemotingUtil.parseRemotePort(this.channel);
    }

    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) this.channel.localAddress();
    }

    public String getLocalIP() {
        return RemotingUtil.parseLocalIP(this.channel);
    }

    public int getLocalPort() {
        return RemotingUtil.parseLocalPort(this.channel);
    }

    public void close(){
        if (closed.compareAndSet(false ,true)){
            try{
                if (this.channel != null){
                    this.channel.close().addListener(future -> {
                        LOGGER.info(
                                        "Close the connection to remote address={}, result={}, cause={}",
                                        RemotingUtil.parseRemoteAddress(Connection.this
                                                .getChannel()), future.isSuccess(), future.cause());
                    });
                }

            }catch (Exception e){
                LOGGER.warn("Exception caught when closing connection {}",
                        RemotingUtil.parseRemoteAddress(Connection.this.getChannel()), e);
            }
        }
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void addPoolKey(String poolKey){
        poolKeys.add(poolKey);
    }

    public Set<String> getPoolKeys(){
        return Sets.newHashSet(poolKeys);
    }

    public void removePoolKey(String poolKey) {
        poolKeys.remove(poolKey);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }


    public void addIdPoolKeyMapping(Object id, String poolKey) {
        this.id2PoolKey.put(id, poolKey);
    }

    public String removeIdPoolKeyMapping(Object id) {
        return this.id2PoolKey.remove(id);
    }

    /**
     * 终端类型
     */
    public static enum TermType{
        APP,

        PC,

        WEB,

        App_test1,
        App_test2,
        App_test3,
        App_test4,
        App_test5,
        App_test6,
        App_test7,
        App_test8,
        App_test9


    }
}
