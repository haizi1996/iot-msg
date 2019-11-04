package com.hailin.iot.leaf.snowflake;

import com.hailin.iot.leaf.common.Endpoint;
import com.hailin.iot.leaf.exception.CheckLastTimeException;
import com.hailin.iot.leaf.util.EndpointUtil;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.ScoredValue;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
public class RedisClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClientService.class);

    //redis自增key
    private static final String REDIS_INCR_KEY = "redis_incr_key";
    @Getter
    private int workerID;
    private String ip;
    private Integer port;

    private static final int IDLE_TIME = 5;

    private static final String WORK_ID_KEY = "snowflake_leaf";


    private RedisClient redisClient;

    private RedisAsyncCommands<byte[], byte[]> asyncCommands;
    private RedisCommands<byte[], byte[]> commands;

    private String redisUrl;

    public RedisClientService(String ip, Integer port, String redisUrl) {
        this.ip = ip;
        this.port = port;
        this.redisUrl = redisUrl;
//        redisClient = RedisClient.create("redis://"+ ip +":" + port + "/1");
        redisClient = RedisClient.create(this.redisUrl);
        commands = redisClient.connect(new ByteArrayCodec()).sync();
        asyncCommands = redisClient.connect(new ByteArrayCodec()).async();
    }

    public boolean init() {
        long now = System.currentTimeMillis();
        List<ScoredValue<byte[]>> members = commands.zrangeWithScores(WORK_ID_KEY.getBytes(), now - (IDLE_TIME << 1), now);
        //第一台机器
        if (CollectionUtils.isEmpty(members)) {

            workerID = commands.incr(REDIS_INCR_KEY.getBytes()).intValue();

            createNode();

            //定时上报本机时间给forever节点
            ScheduledUploadData();
            return true;
        } else {
            List<EndPointWrapper> endPointWrappers = members.stream()
                    .filter(scoredValue -> Objects.nonNull(scoredValue) && ArrayUtils.isNotEmpty(scoredValue.getValue()) )
                    .map(scoredValue -> new EndPointWrapper(EndpointUtil.deSerializationToObj(scoredValue.getValue()), new Double(scoredValue.getScore()).longValue()))
                    .collect(Collectors.toList());

            EndPointWrapper pointWrappers = endPointWrappers.stream()
                    .filter(endPointWrapper -> Objects.equals(endPointWrapper.getIp(), ip) && port == endPointWrapper.getPort())
                    .findAny().orElse(null);
            //有自己的节点
            if (pointWrappers != null) {
                workerID = pointWrappers.getWorkerID();
                if (!checkInitTimeStamp(pointWrappers)) {
                    throw new CheckLastTimeException("init timestamp check error,forever node timestamp gt this node time");
                }
                LOGGER.info("[Old NODE]find forever node have this endpoint ip-{} port-{} workid-{} childnode and start SUCCESS", ip, port, workerID);
            }else {
                if (!checkInitTimeStampByAllNode(endPointWrappers)) {
                    throw new CheckLastTimeException("init timestamp check error,forever node timestamp gt this node time");
                }
                //表示新启动的节点
                workerID = commands.incr(REDIS_INCR_KEY.getBytes()).intValue();
                createNode();
                ScheduledUploadData();
            }

        }
        return true;
    }

    /**
     * 需要检查当前机器的时间与 (sum(node 的时间) / node.size) 的差值在某个阙值之内 这里认为是一个心跳周期
     * @return
     */
    private boolean checkInitTimeStampByAllNode(List<EndPointWrapper> endPointWrappers) {
        long avrgScope = endPointWrappers.stream().mapToLong(EndPointWrapper::getTimeStamp).sum() / endPointWrappers.size();
        return Math.abs(System.currentTimeMillis() - avrgScope) < IDLE_TIME ;
    }


    private boolean checkInitTimeStamp(EndPointWrapper endPointWrapper) {
        //该节点的时间不能小于最后一次上报的时间
        return !(endPointWrapper.getTimeStamp() > System.currentTimeMillis());
    }

    @Getter
    @Setter
    private static class EndPointWrapper extends Endpoint {
        private long timeStamp;

        public EndPointWrapper(Endpoint endpoint, long timeStamp) {
            this.timeStamp = timeStamp;
            setIp(endpoint.getIp());
            setPort(endpoint.getPort());
            setWorkerID(endpoint.getWorkerID());
        }

    }

    private void ScheduledUploadData() {
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
        commands.zadd(WORK_ID_KEY.getBytes(), System.currentTimeMillis(), buildData());
    }

    private byte[] buildData() {
        Endpoint endpoint = new Endpoint(ip, port, workerID);
        return EndpointUtil.serializeToByteArray(endpoint);
    }


    private void createNode() {
        try {
            commands.zadd(WORK_ID_KEY.getBytes(), System.currentTimeMillis(), EndpointUtil.serializeToByteArray(new Endpoint(ip, port, workerID)));
        } catch (Exception e) {
            LOGGER.error("create node error msg {} ", e.getMessage());
            throw e;
        }
    }
}
