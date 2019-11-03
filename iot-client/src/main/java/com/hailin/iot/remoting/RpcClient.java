package com.hailin.iot.remoting;

import com.hailin.iot.common.exception.LifeCycleException;
import com.hailin.iot.common.remoting.ConnectionEventHandler;
import com.hailin.iot.common.remoting.ConnectionEventListener;
import com.hailin.iot.common.remoting.ConnectionEventType;
import com.hailin.iot.common.remoting.ConnectionSelectStrategy;
import com.hailin.iot.common.remoting.RandomSelectStrategy;
import com.hailin.iot.common.remoting.RemotingAddressParser;
import com.hailin.iot.common.remoting.RpcAddressParser;
import com.hailin.iot.common.remoting.RpcConnectionEventHandler;
import com.hailin.iot.common.remoting.RpcRemoting;
import com.hailin.iot.common.remoting.RpcTaskScanner;
import com.hailin.iot.common.remoting.UserProcessor;
import com.hailin.iot.common.remoting.config.IotGenericOption;
import com.hailin.iot.common.remoting.config.switches.GlobalSwitch;
import com.hailin.iot.common.remoting.connection.ReconnectManager;
import com.hailin.iot.common.remoting.connection.Reconnector;
import com.hailin.iot.common.remoting.factory.impl.MqttConnectionFactory;
import com.hailin.iot.common.remoting.monitor.ConnectionMonitorStrategy;
import com.hailin.iot.common.remoting.monitor.DefaultConnectionMonitor;
import com.hailin.iot.common.remoting.monitor.ScheduledDisconnectStrategy;
import com.hailin.iot.remoting.processor.ConnectEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程通讯的客户端
 * @author hailin
 */
public class RpcClient extends AbstractIotClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private final RpcTaskScanner taskScanner;

    private final ConcurrentHashMap<String , UserProcessor<?>> userProcessors;

    private final ConnectionEventHandler connectionEventHandler;

    private final ConnectionEventListener connectionEventListener;

    private DefaultClientConnectionManager connectionManager;

    private Reconnector reconnectManager;
    private RemotingAddressParser addressParser;
    private DefaultConnectionMonitor connectionMonitor;
    private ConnectionMonitorStrategy monitorStrategy;


    protected RpcRemoting rpcRemoting;

    public RpcClient() {
        this.taskScanner = new RpcTaskScanner();
        this.userProcessors = new ConcurrentHashMap<>();
        this.connectionEventHandler = new RpcConnectionEventHandler();
        this.connectionEventListener = new ConnectionEventListener();
        connectionEventListener.addConnectionEventProcessor(ConnectionEventType.CONNECT , new ConnectEventProcessor());
    }

    @Override
    public void shutdown(){
        super.shutdown();
        this.connectionManager.shutdown();
        LOGGER.warn("Close all connections from client side!");
        this.taskScanner.shutdown();
        LOGGER.warn("Rpc client shutdown!");

        if (reconnectManager != null){
            reconnectManager.shutdown();
        }
    }

    @Override
    public void startup() throws LifeCycleException {
        super.startup();
        if (this.addressParser == null){
            this.addressParser = new RpcAddressParser();
        }

        ConnectionSelectStrategy strategy = option(IotGenericOption.CONNECTION_SELECT_STRATEGY);
        if (Objects.isNull(strategy)){
            strategy = new RandomSelectStrategy(switches());
        }
        this.connectionManager = new DefaultClientConnectionManager(strategy ,
                new MqttConnectionFactory(userProcessors , this)
                , connectionEventHandler , connectionEventListener ,switches());
        this.connectionManager.setAddressParser(addressParser);
        this.connectionManager.startup();

        this.rpcRemoting = new RpcClientRemoting( this.addressParser , this.connectionManager );
        this.taskScanner.add(this.connectionManager);
        this.taskScanner.startup();
        if (switches().isOn(GlobalSwitch.CONN_MONITOR_SWITCH)) {
            if (monitorStrategy == null) {
                connectionMonitor = new DefaultConnectionMonitor(new ScheduledDisconnectStrategy(),
                        this.connectionManager);
            } else {
                connectionMonitor = new DefaultConnectionMonitor(monitorStrategy,
                        this.connectionManager);
            }
            connectionMonitor.startup();
            LOGGER.warn("Switch on connection monitor");
        }
        if (switches().isOn(GlobalSwitch.CONN_RECONNECT_SWITCH)) {
            reconnectManager = new ReconnectManager(connectionManager);
            reconnectManager.startup();

            connectionEventHandler.setReconnectManager(reconnectManager);
            LOGGER.warn("Switch on reconnect manager");
        }
    }

}
