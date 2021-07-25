package com.codedawn.vital.server.handler;

import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionEventType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-28 9:45
 */
public class ConnectionEventHandler extends ChannelInboundHandlerAdapter {

    private static Logger log = LoggerFactory.getLogger(ConnectionEventHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Attribute<Connection> attr = ctx.channel().attr(Connection.CONNECTION);
        if (attr != null) {
            Connection connection = attr.get();
            if (connection != null) {
                    userEventTriggered(ctx,ConnectionEventType.CLOSE);
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof ConnectionEventType) {
            ConnectionEventType event = (ConnectionEventType) evt;

            switch (event) {
                case CLOSE:
                    break;
                case CONNECT:
                    break;
                case CONNECT_FAILED:
                    break;
                default:
                    log.info("unknown connectionEventType");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
