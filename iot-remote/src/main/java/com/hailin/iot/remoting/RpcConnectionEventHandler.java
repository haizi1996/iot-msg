package com.hailin.iot.remoting;

import com.hailin.iot.remoting.config.switches.GlobalSwitch;
import com.hailin.iot.remoting.connection.Connection;
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
//        Connection connection = ctx.channel().attr(Connection.CONNECTION).get();
//        if (connection != null){
//            this.getConnectionManager().remove(connection);
//        }
        // 触发一个连接关闭的时间
        ctx.channel().pipeline().fireUserEventTriggered(ConnectionEventType.CLOSE);
        super.channelInactive(ctx);
    }
}
