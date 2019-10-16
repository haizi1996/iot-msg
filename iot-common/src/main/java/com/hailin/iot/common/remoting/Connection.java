package com.hailin.iot.common.remoting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.Channel;

/**
 * 一个抽象的socker channel
 * @author hailin
 */
public class Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);

    private Channel channel;

}
