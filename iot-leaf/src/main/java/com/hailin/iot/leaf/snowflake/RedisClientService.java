package com.hailin.iot.leaf.snowflake;

import com.hailin.iot.leaf.common.Endpoint;
import com.hailin.iot.leaf.util.EndpointUtil;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


@Slf4j
public class RedisClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClientService.class);
    private String zk_AddressNode = null;//保存自身的key  ip:port-000000001
    private String listenAddress = null;//保存自身的key ip:port

    //redis自增key
    private static final String REDIS_INCR_KEY = "redis_incr_key";
    @Getter
    private int workerID;
    private String ip;
    private Integer port;
    private String connectionString;
    private long lastUpdateTime;

    private static final int IDLE_TIME = 5;

    private static final String WORK_ID_KEY = "snowflake_leaf";


    private RedisClient redisClient;

    private RedisAsyncCommands<byte[] , byte[]> asyncCommands;
    private RedisCommands<byte[] , byte[]> commands;

    private String redisUrl;

    public RedisClientService(String ip , Integer port , String redisUrl) {
        this.ip = ip;
        this.port = port;
        this.redisUrl = redisUrl;
//        redisClient = RedisClient.create("redis://"+ ip +":" + port + "/1");
        redisClient = RedisClient.create(redisUrl);
        commands =  redisClient.connect(new ByteArrayCodec()).sync();
        asyncCommands =  redisClient.connect(new ByteArrayCodec()).async();
    }

    public boolean init() {
        long now = System.currentTimeMillis();
        RedisFuture<List<byte[]>> future = asyncCommands.zrange(WORK_ID_KEY.getBytes() ,  now - (IDLE_TIME << 1)  , now);
        List<byte[]> bytes = null;
        try {
            bytes = future.get();
            //第一台机器
            if(CollectionUtils.isEmpty(bytes)){

                commands.incr(REDIS_INCR_KEY.getBytes());

                createNode();
                //worker id 默认是0
                updateLocalWorkerID(workerID);
                //定时上报本机时间给forever节点
                ScheduledUploadData();
            }
        } catch (InterruptedException e) {
            log.error("" , e);
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            log.error("" , e);
            throw new RuntimeException(e);
        }


    }

    private void updateLocalWorkerID(int workerID) {
    }

    private void ScheduledUploadData( ) {
        Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "schedule-upload-time");
                thread.setDaemon(true);
                return thread;
            }
        }).scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                updateNewData();
            }
        }, 1L, 3L, TimeUnit.SECONDS);//每3s上报数据

    }

    private void updateNewData() {
        commands
    }

    private byte[] buildData()  {
        Endpoint endpoint = new Endpoint(ip, port, System.currentTimeMillis());
        return EndpointUtil.serializeToByteArray(endpoint);
    }

    private Endpoint deBuildData(byte[] data) {
        if(ArrayUtils.isEmpty(data)){
            return null;
        }
        return EndpointUtil.deSerializationToObj(data);
    }

    private void createNode() {
        try {
            commands.zadd(WORK_ID_KEY.getBytes() , System.currentTimeMillis() , EndpointUtil.serializeToByteArray(new Endpoint(ip , port,System.currentTimeMillis())));
        } catch (Exception e) {
            LOGGER.error("create node error msg {} ", e.getMessage());
            throw e;
        }
    }
}
