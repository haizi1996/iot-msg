package com.hailin.iot.remoting;


import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.util.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * default biz context
 *
 * @author xiaomin.cxm
 * @version $Id: DefaultBizContext.java, v 0.1 Jan 7, 2016 10:42:30 AM xiaomin.cxm Exp $
 */
public class DefaultBizContext implements BizContext {

    private RemotingContext remotingCtx;


    public DefaultBizContext(RemotingContext remotingCtx) {
        this.remotingCtx = remotingCtx;
    }

    /**
     * get remoting context
     *
     * @return RemotingContext
     */
    protected RemotingContext getRemotingCtx() {
        return this.remotingCtx;
    }


    @Override
    public String getRemoteAddress() {
        if (null != this.remotingCtx) {
            ChannelHandlerContext channelCtx = this.remotingCtx.getChannelContext();
            Channel channel = channelCtx.channel();
            if (null != channel) {
                return RemotingUtil.parseRemoteAddress(channel);
            }
        }
        return "UNKNOWN_ADDRESS";
    }


    @Override
    public String getRemoteHost() {
        if (null != this.remotingCtx) {
            ChannelHandlerContext channelCtx = this.remotingCtx.getChannelContext();
            Channel channel = channelCtx.channel();
            if (null != channel) {
                return RemotingUtil.parseRemoteIP(channel);
            }
        }
        return "UNKNOWN_HOST";
    }


    @Override
    public Integer getRemotePort() {
        if (null != this.remotingCtx) {
            ChannelHandlerContext channelCtx = this.remotingCtx.getChannelContext();
            Channel channel = channelCtx.channel();
            if (null != channel) {
                return RemotingUtil.parseRemotePort(channel);
            }
        }
        return -1;
    }


    @Override
    public Connection getConnection() {
        if (null != this.remotingCtx) {
            return this.remotingCtx.getConnection();
        }
        return null;
    }


    @Override
    public boolean isRequestTimeout() {
        return this.remotingCtx.isRequestTimeout();
    }


    @Override
    public int getClientTimeout() {
        return this.remotingCtx.getTimeout();
    }

    /**
     * get the arrive time stamp
     *
     * @return
     */
    @Override
    public long getArriveTimestamp() {
        return this.remotingCtx.getArriveTimestamp();
    }


    @Override
    public void put(String key, String value) {
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public InvokeContext getInvokeContext() {
        return this.remotingCtx.getInvokeContext();
    }
}
