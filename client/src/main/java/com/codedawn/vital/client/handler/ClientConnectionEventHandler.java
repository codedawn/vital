package com.codedawn.vital.client.handler;

import com.codedawn.vital.client.session.ClientConnectionEventListener;
import com.codedawn.vital.server.callback.ChannelStatusCallBack;
import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionEventType;
import com.codedawn.vital.server.util.AddressUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * connectionHandler，处理连接
 * @author codedawn
 * @date 2021-07-28 9:45
 */
public class ClientConnectionEventHandler extends ChannelInboundHandlerAdapter {

    private static Logger log = LoggerFactory.getLogger(ClientConnectionEventHandler.class);

    private ClientConnectionEventListener clientConnectionEventListener;

    private ChannelStatusCallBack channelStatusCallBack;

    public ClientConnectionEventListener getConnectionEventListener() {
        return clientConnectionEventListener;
    }

    public ClientConnectionEventHandler setConnectionEventListener(ClientConnectionEventListener clientConnectionEventListener) {
        this.clientConnectionEventListener = clientConnectionEventListener;
        return this;
    }

    public ChannelStatusCallBack getChannelStatusCallBack() {
        return channelStatusCallBack;
    }

    public ClientConnectionEventHandler(ClientConnectionEventListener clientConnectionEventListener) {
        this.clientConnectionEventListener = clientConnectionEventListener;
    }

    public ClientConnectionEventHandler(ClientConnectionEventListener clientConnectionEventListener, ChannelStatusCallBack channelStatusCallBack) {
        this.clientConnectionEventListener = clientConnectionEventListener;
        this.channelStatusCallBack = channelStatusCallBack;
    }

    public ClientConnectionEventHandler setChannelStatusCallBack(ChannelStatusCallBack channelStatusCallBack) {
        this.channelStatusCallBack = channelStatusCallBack;
        return this;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (channelStatusCallBack != null) {
            channelStatusCallBack.close(ctx.channel());
        }
        Attribute<Connection> attr = ctx.channel().attr(Connection.CONNECTION);
        if (attr != null) {
            Connection connection = attr.get();
            if (connection != null) {
                    userEventTriggered(ctx,ConnectionEventType.CLOSE);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (channelStatusCallBack != null) {
            channelStatusCallBack.open(ctx.channel());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof ConnectionEventType) {
            ConnectionEventType event = (ConnectionEventType) evt;
            String remoteAddress = AddressUtil.parseRemoteAddress(ctx.channel());
            Attribute<Connection> attr = ctx.channel().attr(Connection.CONNECTION);
            Connection connection = null;
            if (attr != null) {
                connection = attr.get();
                if (connection == null) {
                    log.info("connection is null 在userEventTriggered中");
                }
            }
            switch (event) {
                case CLOSE:
                    log.info("onEvent CLOSE");
                    onEvent(remoteAddress,connection, event);
                    break;
                case CONNECT:
                    log.info("onEvent CONNECT");
                    onEvent(remoteAddress,connection, event);
                    break;
                case CONNECT_FAILED:
                    log.info("onEvent CONNECT_FAILED");
                    onEvent(remoteAddress,connection, event);
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
         String remoteAddress = AddressUtil.parseRemoteAddress(ctx.channel());
         String localAddress = AddressUtil.parseLocalAddress(ctx.channel());
        log.warn("ExceptionCaught : local[{}], remote[{}], 断开了connection! Cause[{}:{}]",
                        localAddress, remoteAddress, cause.getClass().getSimpleName(), cause.getMessage());
        ctx.channel().close();
    }

    private void onEvent(String remoteAddress, Connection connection, ConnectionEventType eventType) {
        //没有设置线程池使用io线程
        clientConnectionEventListener.onEvent(remoteAddress,connection,eventType);
    }
}
