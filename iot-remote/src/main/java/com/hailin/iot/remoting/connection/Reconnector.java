package com.hailin.iot.remoting.connection;

import com.hailin.iot.remoting.LifeCycle;
import com.hailin.iot.remoting.Url;

/**
 * 从连接管理的接口
 * @author hailin
 */
public interface Reconnector extends LifeCycle {

    void reconnect(Url url);

    void disableReconnect(Url url);

    void enableReconnect(Url url);
}
