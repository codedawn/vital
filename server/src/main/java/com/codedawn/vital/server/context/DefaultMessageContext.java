package com.codedawn.vital.server.context;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author codedawn
 * @date 2021-07-24 23:05
 */
public class DefaultMessageContext implements MessageContext {
    private final ChannelHandlerContext channelHandlerContext;


    public DefaultMessageContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;

    }

    @Override
    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
