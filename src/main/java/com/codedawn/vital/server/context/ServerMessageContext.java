package com.codedawn.vital.server.context;

import com.codedawn.vital.context.MessageContext;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author codedawn
 * @date 2021-07-24 23:05
 */
public class ServerMessageContext implements MessageContext {
    private ChannelHandlerContext channelHandlerContext;


    public ServerMessageContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;

    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
