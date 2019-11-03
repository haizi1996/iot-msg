package com.hailin.iot.common.remoting;

import com.hailin.iot.common.remoting.config.switches.GlobalSwitch;
import com.hailin.iot.common.remoting.connection.Connection;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;


/**
 * rpc的连接时间处理器
 * @author hailin
 */
@Slf4j
public class RpcConnectionEventHandler extends ConnectionEventHandler {

    public RpcConnectionEventHandler() {
        super();
    }

    public RpcConnectionEventHandler(GlobalSwitch globalSwitch){
        super(globalSwitch);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Connection connection = ctx.channel().attr(Connection.CONNECTION).get();
        if (connection != null){
            this.getConnectionManager().remove(connection);
        }
        super.channelInactive(ctx);
    }
}
