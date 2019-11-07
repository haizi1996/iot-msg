package com.hailin.iot.remoting;

import com.hailin.iot.remoting.config.Configs;
import com.hailin.iot.remoting.config.switches.GlobalSwitch;
import com.hailin.iot.remoting.connection.Connection;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSelectStrategy implements ConnectionSelectStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomSelectStrategy.class);;

    private static final int MAX_TIMES = 5;

    private final Random random = new Random();

    private final GlobalSwitch globalSwitch;

    public RandomSelectStrategy(GlobalSwitch globalSwitch) {
        this.globalSwitch = globalSwitch;
    }

    @Override
    public Connection select(List<Connection> connections) {
        if (CollectionUtils.isEmpty(connections)){
            return null;
        }
        try {
            Connection result ;
            if (this.globalSwitch == null
            || !this.globalSwitch.isOn(GlobalSwitch.CONN_MONITOR_SWITCH)){
                result =randomGet(connections);
            }else {
                List<Connection> serviceStatusOnConnections = new ArrayList<Connection>();
                for (Connection conn : connections) {
                    String serviceStatus = (String) conn.getAttribute(Configs.CONN_SERVICE_STATUS);
                    if (!StringUtils.equals(serviceStatus, Configs.CONN_SERVICE_STATUS_OFF)) {
                        serviceStatusOnConnections.add(conn);
                    }
                }
                if (serviceStatusOnConnections.size() == 0) {
                    throw new Exception(
                            "No available connection when select in RandomSelectStrategy.");
                }
                result = randomGet(serviceStatusOnConnections);
            }
            return  result;
        }catch (Throwable e){
            LOGGER.error("Choose connection failed using RandomSelectStrategy!", e);
            return null;
        }
    }

    private Connection randomGet(List<Connection> connections) {
        if (CollectionUtils.isEmpty(connections)){
            return null;
        }
        int size = connections.size() , tries = 0;
        Connection result = null;
        while (result == null || ! result.isFine() && tries++ < MAX_TIMES){
            result = connections.get(this.random.nextInt(size));
        }
        if (result != null && ! result.isFine()){
            result = null;
        }
        return result;
    }
}
