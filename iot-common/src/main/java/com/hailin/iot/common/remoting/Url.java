package com.hailin.iot.common.remoting;

import com.hailin.iot.common.remoting.config.Configs;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.SoftReference;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程URL的定义
 * @author zhanghailin
 */
@Getter
@Setter
public class Url {

    //原始的URL
    private String originUrl;

    private String ip;

    private int port ;

    //唯一主键
    private String uniqueKey;

    //连接超时时间
    @Getter
    private int connectTimeout ;

    // url 参数 协议
    @Getter
    private byte protocol;

    //url 参数 版本
    private byte version;

    //连接数
    private int connNum;

    //是否需要连接预热
    private boolean connwarmup;

    // url所有的参数
    private Properties properties;

    protected Url(String originUrl) {
        this.originUrl = originUrl;
    }
    public Url(String ip, int port) {
        this(ip + RemotingAddressParser.COLON + port);
        this.ip = ip;
        this.port = port;
        this.uniqueKey = this.originUrl;
    }

    public Url(String originUrl, String ip, int port) {
        this(originUrl);
        this.ip = ip;
        this.port = port;
        this.uniqueKey = ip + RemotingAddressParser.COLON + port;
    }

    public Url(String originUrl, String ip, int port, Properties properties) {
        this(originUrl, ip, port);
        this.properties = properties;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Url url = (Url) obj;
        if (this.getOriginUrl().equals(url.getOriginUrl())) {
            return true;
        } else {
            return false;
        }
    }

    public void setConnNum(int connNum) {
        if (connNum <= 0 || connNum > Configs.MAX_CONN_NUM_PER_URL) {
            throw new IllegalArgumentException("Illegal value of connection number [" + connNum
                    + "], must be an integer between ["
                    + Configs.DEFAULT_CONN_NUM_PER_URL + ", "
                    + Configs.MAX_CONN_NUM_PER_URL + "].");
        }
        this.connNum = connNum;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.getOriginUrl() == null) ? 0 : this.getOriginUrl().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Origin url [").append(this.originUrl).append("], Unique key [")
                .append(this.uniqueKey).append("].");
        return sb.toString();
    }

    public static ConcurrentHashMap<String, SoftReference<Url>> parsedUrls  = new ConcurrentHashMap<String, SoftReference<Url>>();

    //是否是已连接
    public static volatile boolean isCollected = false;

    private static final Logger logger  = LoggerFactory.getLogger(Url.class);

    @Override
    protected void finalize() {
        try {
            isCollected = true;
            parsedUrls.remove(this.getOriginUrl());
        } catch (Exception e) {
            logger.error("Exception occurred when do finalize for Url [{}].", this.getOriginUrl(),
                    e);
        }
    }

    public boolean isConnWarmup() {
        return this.connwarmup;
    }
}
