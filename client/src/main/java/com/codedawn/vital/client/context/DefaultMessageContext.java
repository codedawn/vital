package com.codedawn.vital.client.context;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author codedawn
 * @date 2021-07-24 23:05
 */
public class DefaultMessageContext implements MessageContext, com.codedawn.vital.server.context.MessageContext {
    private ChannelHandlerContext channelHandlerContext;


    public DefaultMessageContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;

    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
