package com.hailin.iot.common.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;

/**
 * 远程相关的工具类
 * @author hailin
 */
public class RemotingUtil {

    /**
     * 解析channel的远程地址
     */
    public static String parseRemoteAddress(final Channel channel){{}
        if (Objects.isNull(channel)){
            return StringUtils.EMPTY;
        }
        final SocketAddress remote = channel.remoteAddress();
        return doParse(remote != null ? remote.toString().trim() : StringUtils.EMPTY);
    }


    /**
     * 解析channel的本地 网络地址
     */
    public static String parseLocalAddress(final Channel channel){
        if (Objects.isNull(channel)){
            return StringUtils.EMPTY;
        }
        final SocketAddress remote = channel.localAddress();
        return doParse(remote != null ? remote.toString().trim() : StringUtils.EMPTY);
    }

    public static String parseRemoteIP(final Channel channel) {
        if (null == channel) {
            return StringUtils.EMPTY;
        }
        final InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        if (remote != null) {
            return remote.getAddress().getHostAddress();
        }
        return StringUtils.EMPTY;
    }
    public static String parseRemoteHostName(final Channel channel) {
        if (null == channel) {
            return StringUtils.EMPTY;
        }
        final InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        if (remote != null) {
            return remote.getAddress().getHostName();
        }
        return StringUtils.EMPTY;
    }

    public static String parseLocalIP(final Channel channel) {
        if (null == channel) {
            return StringUtils.EMPTY;
        }
        final InetSocketAddress local = (InetSocketAddress) channel.localAddress();
        if (local != null) {
            return local.getAddress().getHostAddress();
        }
        return StringUtils.EMPTY;
    }

    public static int parseRemotePort(final Channel channel) {
        if (null == channel) {
            return -1;
        }
        final InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        if (remote != null) {
            return remote.getPort();
        }
        return -1;
    }

    public static int parseLocalPort(final Channel channel) {
        if (null == channel) {
            return -1;
        }
        final InetSocketAddress local = (InetSocketAddress) channel.localAddress();
        if (local != null) {
            return local.getPort();
        }
        return -1;
    }

    public static String parseSocketAddressToString(SocketAddress socketAddress) {
        if (socketAddress != null) {
            return doParse(socketAddress.toString().trim());
        }
        return StringUtils.EMPTY;
    }

    private static String doParse(String addr) {
        if (StringUtils.isBlank(addr)) {
            return StringUtils.EMPTY;
        }
        if (addr.charAt(0) == '/') {
            return addr.substring(1);
        } else {
            int len = addr.length();
            for (int i = 1; i < len; ++i) {
                if (addr.charAt(i) == '/') {
                    return addr.substring(i + 1);
                }
            }
            return addr;
        }
    }
}
