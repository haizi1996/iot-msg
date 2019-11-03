package com.hailin.iot.common.remoting;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public class RpcHandler extends ChannelInboundHandlerAdapter {

    private boolean serverSide ;

    private ConcurrentHashMap<String , UserProcessor<?>> userProcessors;

    public RpcHandler() {
        serverSide = false;
    }

    public RpcHandler(ConcurrentHashMap<String, UserProcessor<?>> userProcessors) {
        this(false , userProcessors);
    }

    public RpcHandler(boolean serverSide, ConcurrentHashMap<String, UserProcessor<?>> userProcessors) {
        this.serverSide = serverSide;
        this.userProcessors = userProcessors;
    }



    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
