package com.hailin.iot.common.remoting;

import com.hailin.iot.common.remoting.command.CommandFactory;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 远程通讯的基类
 * @author hailin
 */
public abstract class BaseRemoting {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseRemoting.class);

    @Getter
    protected CommandFactory    commandFactory;

    public BaseRemoting(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

}
