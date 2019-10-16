package com.hailin.iot.common.util;

import com.hailin.iot.common.remoting.config.ConfigManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ThreadFactory;

/**
 * Netty事件循环的工具类
 * @author hailin
 */
public class NettyEventLoopUtil {

    //检查是否可以使用epoll
    private static boolean epollEnabled = ConfigManager.netty_epoll() && Epoll.isAvailable();

    public static EventLoopGroup newEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        return epollEnabled ? new EpollEventLoopGroup(nThreads, threadFactory)
                : new NioEventLoopGroup(nThreads, threadFactory);
    }

    public static Class<? extends SocketChannel> getClientSocketChannelClass() {
        return epollEnabled ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        return epollEnabled ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    /**
     * 采用epoll的水平触发模式
     */
    public static void enableTriggeredMode(ServerBootstrap serverBootstrap) {
        if (epollEnabled) {
            if (ConfigManager.netty_epoll_lt_enabled()) {
                serverBootstrap.childOption(EpollChannelOption.EPOLL_MODE,
                        EpollMode.LEVEL_TRIGGERED);
            } else {
                serverBootstrap
                        .childOption(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
            }
        }
    }
}
