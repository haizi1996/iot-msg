package com.hailin.iot.remoting.util;


import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.future.InvokeFuture;
import io.netty.channel.Channel;
import io.netty.util.Attribute;

public class ConnectionUtil {

    public static Connection getConnectionFromChannel(Channel channel) {
        if (channel == null) {
            return null;
        }

        Attribute<Connection> connAttr = channel.attr(Connection.CONNECTION);
        if (connAttr != null) {
            Connection connection = connAttr.get();
            return connection;
        }
        return null;
    }

    public static void addIdPoolKeyMapping(Integer id, String group, Channel channel) {
        Connection connection = getConnectionFromChannel(channel);
        if (connection != null) {
            connection.addIdPoolKeyMapping(id, group);
        }
    }

    public static String removeIdPoolKeyMapping(Integer id, Channel channel) {
        Connection connection = getConnectionFromChannel(channel);
        if (connection != null) {
            return connection.removeIdPoolKeyMapping(id);
        }

        return null;
    }


}
