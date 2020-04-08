package com.hailin.iot.remoting;

import com.hailin.iot.common.exception.RemotingException;
import com.hailin.iot.remoting.config.Configurable;
import com.hailin.iot.remoting.connection.Connection;

/**
 * 客户端通讯接口
 * @author hailin
 */
public interface IotClient extends LifeCycle , Configurable {

    Connection createStandaloneConnection(String ip , int port, int connectTimeout) throws RemotingException;
    Connection createStandaloneConnection(String address , int connectTimeout) throws RemotingException;
}
