package com.hailin.iot.leaf.snowflake;

import com.google.common.base.Preconditions;
import com.hailin.iot.leaf.IDGen;
import com.hailin.iot.leaf.common.Result;
import com.hailin.iot.leaf.common.Status;
import com.hailin.iot.leaf.util.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@Getter
@Slf4j
public class SnowflakeIDGenImpl implements IDGen {

    @Override
    public boolean init() {
        return true;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeIDGenImpl.class);

    private final long twepoch;
    private final long workerIdBits = 10L;
    private final long maxWorkerId = ~(-1L << workerIdBits);//最大能够分配的workerid =1023
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    private final long sequenceMask = ~(-1L << sequenceBits);
    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private static final Random RANDOM = new Random();


    public SnowflakeIDGenImpl(String redisServer, int port) {
        //Thu Nov 04 2010 09:42:54 GMT+0800 (中国标准时间)
        this(redisServer, port, 1288834974657L);
    }

    /**
     * @param zkAddress zk地址
     * @param port      snowflake监听端口
     * @param twepoch   起始的时间戳
     */
    public SnowflakeIDGenImpl(String zkAddress, int port, long twepoch) {
        this.twepoch = twepoch;
        Preconditions.checkArgument(timeGen() > twepoch, "Snowflake not support twepoch gt currentTime");
        final String ip = Utils.getIp();
        RedisClientService service = new RedisClientService(ip, port);
        LOGGER.info("twepoch:{} ,ip:{} ,redis Address:{} port:{}", twepoch, ip, zkAddress, port);
        boolean initFlag = service.init();
        if (initFlag) {
            workerId = service.getWorkerID();
            LOGGER.info("START SUCCESS USE REDIS WORKERID-{}", workerId);
        } else {
            Preconditions.checkArgument(initFlag, "Snowflake Id Gen is not init ok");
        }
        Preconditions.checkArgument(workerId >= 0 && workerId <= maxWorkerId, "workerID must gte 0 and lte 1023");
    }
    @Override
    public synchronized Result get(String key) {
        //获取当前时间
        long timestamp = timeGen();

        if (timestamp < lastTimestamp){
            long offset = lastTimestamp - timestamp ;
            if (offset <= 5){
                try {
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp){
                        return  new Result(-1, Status.EXCEPTION);
                    }
                }catch (InterruptedException e){
                    LOGGER.error("wait interrupted");
                    return new Result(-2, Status.EXCEPTION);
                }
            }else {
                return new Result(-3 , Status.EXCEPTION);
            }
        }
        if (lastTimestamp == timestamp){
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                //seq 为0的时候表示是下一毫秒时间开始对seq做随机
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        }else {
            //如果是新的ms开始
            sequence = RANDOM.nextInt(100);
        }
        lastTimestamp = timestamp;
        long id = ((timestamp - twepoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
        return new Result(id, Status.SUCCESS);
    }
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
