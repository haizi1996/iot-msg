package com.hailin.iot.leaf.snowflake;

import com.hailin.iot.leaf.common.Endpoint;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisHashCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.protocol.RedisCommand;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class RedisClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClientService.class);
    private String zk_AddressNode = null;//保存自身的key  ip:port-000000001
    private String listenAddress = null;//保存自身的key ip:port
    @Getter
    private int workerID;
    private String ip;
    private Integer port;
    private String connectionString;
    private long lastUpdateTime;

    private static final int IDLE_TIME = 5;

    private static final String WORK_ID_KEY = "snowflake_leaf";

    private static final RedisClientService INSTANCE = null;

    private RedisClient redisClient;

    public RedisClientService(String ip , Integer port) {
        this.ip = ip;
        this.port = port;
        redisClient = RedisClient.create("redis://"+ ip +":" + port + "/1");
    }

    public boolean init() throws ExecutionException, InterruptedException {
        long now = System.currentTimeMillis();
        RedisAsyncCommands<byte[] , byte[]> commands =  redisClient.connect(new ByteArrayCodec()).async();
        RedisFuture<List<byte[]>> future = commands.zrange(WORK_ID_KEY.getBytes() ,  now - (IDLE_TIME << 1)  , now);
        List<byte[]> bytes = future.get();
        //第一台机器
        if(CollectionUtils.isEmpty(bytes)){

        }

    }
}
