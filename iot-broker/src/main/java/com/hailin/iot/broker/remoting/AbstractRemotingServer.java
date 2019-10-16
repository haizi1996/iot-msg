package com.hailin.iot.broker.remoting;

import com.hailin.iot.common.remoting.AbstractLifeCycle;
import com.hailin.iot.common.remoting.config.BoltOption;
import com.hailin.iot.common.remoting.config.BoltOptions;
import com.hailin.iot.common.remoting.config.Configurable;
import com.hailin.iot.common.exception.LifeCycleException;
import com.hailin.iot.common.remoting.config.ConfigManager;
import com.hailin.iot.common.remoting.config.ConfigType;
import com.hailin.iot.common.remoting.config.configs.ConfigContainer;
import com.hailin.iot.common.remoting.config.configs.ConfigItem;
import com.hailin.iot.common.remoting.config.configs.ConfigurableInstance;
import com.hailin.iot.common.remoting.config.configs.DefaultConfigContainer;
import com.hailin.iot.common.remoting.config.switches.GlobalSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

/**
 * 抽象的远程服务类
 * @author hailin
 */
public abstract class AbstractRemotingServer extends AbstractLifeCycle implements RemotingServer , ConfigurableInstance {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRemotingServer.class);
    private String                ip;
    private int                   port;

    private final BoltOptions options;
    private final ConfigType configType;
    private final GlobalSwitch globalSwitch;
    private final ConfigContainer configContainer;

    public AbstractRemotingServer(int port) {
        this(new InetSocketAddress(port).getAddress().getHostAddress(), port);
    }

    public AbstractRemotingServer(String ip, int port) {
        this.ip = ip;
        this.port = port;

        this.options = new BoltOptions();
        this.configType = ConfigType.SERVER_SIDE;
        this.globalSwitch = new GlobalSwitch();
        this.configContainer = new DefaultConfigContainer();
    }

    @Override
    public String ip() {
        return ip;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public void registerDefaultExecutor(byte protocolCode, ExecutorService executor) {

    }

    @Override
    public void startup() throws LifeCycleException {
        super.startup();
        try {
            doInit();

            LOGGER.warn("Prepare to start server on port {} ", port);
            if (doStart()) {
                LOGGER.warn("Server started on port {}", port);
            } else {
                LOGGER.warn("Failed starting server on port {}", port);
                throw new LifeCycleException("Failed starting server on port: " + port);
            }
        } catch (Throwable t) {
            this.shutdown();// do stop to ensure close resources created during doInit()
            throw new IllegalStateException("ERROR: Failed to start the Server!", t);
        }
    }


    
    @Override
    public void shutdown() throws LifeCycleException {
        super.shutdown();
        if (!doStop()) {
            throw new LifeCycleException("doStop fail");
        }
    }

    protected abstract void doInit();

    protected abstract boolean doStart() throws InterruptedException;

    protected abstract boolean doStop();

    @Override
    public <T> T option(BoltOption<T> option) {
        return options.option(option);
    }

    @Override
    public <T> Configurable option(BoltOption<T> option, T value) {
        options.option(option, value);
        return this;
    }

    @Override
    public ConfigContainer conf() {
        return this.configContainer;
    }

    @Override
    public GlobalSwitch switches() {
        return this.globalSwitch;
    }
    @Override
    public void initWriteBufferWaterMark(int low, int high) {
        this.configContainer.set(configType, ConfigItem.NETTY_BUFFER_LOW_WATER_MARK, low);
        this.configContainer.set(configType, ConfigItem.NETTY_BUFFER_HIGH_WATER_MARK, high);
    }

    @Override
    public int netty_buffer_low_watermark() {
        Object config = configContainer.get(configType, ConfigItem.NETTY_BUFFER_LOW_WATER_MARK);
        if (config != null) {
            return (Integer) config;
        } else {
            return ConfigManager.netty_buffer_low_watermark();
        }
    }

    @Override
    public int netty_buffer_high_watermark() {
        Object config = configContainer.get(configType, ConfigItem.NETTY_BUFFER_HIGH_WATER_MARK);
        if (config != null) {
            return (Integer) config;
        } else {
            return ConfigManager.netty_buffer_high_watermark();
        }
    }

}
