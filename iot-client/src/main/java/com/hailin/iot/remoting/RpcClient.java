package com.hailin.iot.remoting;

import com.hailin.iot.common.exception.LifeCycleException;
import com.hailin.iot.common.exception.RemotingException;
import com.hailin.iot.remoting.config.IotGenericOption;
import com.hailin.iot.remoting.config.switches.GlobalSwitch;
import com.hailin.iot.remoting.connection.Connection;
import com.hailin.iot.remoting.connection.MqttConnectionFactory;
import com.hailin.iot.remoting.connection.ReconnectManager;
import com.hailin.iot.remoting.connection.Reconnector;
import com.hailin.iot.remoting.handler.MqttMessageServerHandler;
import com.hailin.iot.remoting.monitor.ConnectionMonitorStrategy;
import com.hailin.iot.remoting.monitor.DefaultConnectionMonitor;
import com.hailin.iot.remoting.monitor.ScheduledDisconnectStrategy;
import com.hailin.iot.remoting.processor.ConnectEventProcessor;
import com.hailin.iot.remoting.processor.UserProcessorRegisterHelper;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程通讯的客户端
 * @author hailin
 */
public class RpcClient extends AbstractIotClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private final RpcTaskScanner taskScanner;

    private final ConcurrentHashMap<MqttMessageType, UserProcessor<?>> userProcessors;

    private final ConnectionEventHandler connectionEventHandler;

    private final ConnectionEventListener connectionEventListener;

    private DefaultClientConnectionManager connectionManager;

    private Reconnector reconnectManager;
    private RemotingAddressParser addressParser;
    private DefaultConnectionMonitor connectionMonitor;
    @Setter
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
                new MqttConnectionFactory(userProcessors , connectionManager,this , MqttMessageServerHandler.getHandler())
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
    @Override
    public Connection createStandaloneConnection(String ip , int port, int connectTimeout)
            throws RemotingException {
        return createStandaloneConnection(ip + ":" + port , connectTimeout);
    }


    public void sendMqttMessage(MqttMessage message){
    }

    @Override
    public Connection createStandaloneConnection(String address, int connectTimeout) throws RemotingException {
        return this.connectionManager.create(address, connectTimeout);
    }

    @Override
    public void registerUserProcessor(UserProcessor<?> processor) {
        UserProcessorRegisterHelper.registerUserProcessor(processor, this.userProcessors);
    }
}
