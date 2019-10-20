package com.hailin.iot.common.remoting.connection;

import com.hailin.iot.common.remoting.LifeCycle;
import com.hailin.iot.common.remoting.Url;

/**
 * 从连接管理的接口
 * @author hailin
 */
public interface Reconnector extends LifeCycle {

    void reconnect(Url url);

    void disableReconnect(Url url);

    void enableReconnect(Url url);
}
