package com.hailin.iot.remoting;

import com.hailin.iot.common.exception.RemotingException;
import com.hailin.iot.common.remoting.LifeCycle;
import com.hailin.iot.common.remoting.config.Configurable;
import com.hailin.iot.common.remoting.connection.Connection;

/**
 * 客户端通讯接口
 * @author hailin
 */
public interface IotClient extends LifeCycle , Configurable {


    Connection createStandaloneConnection(String address, int connectTimeout) throws RemotingException;
}
