package com.codedawn.vital.server.context;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author codedawn
 * @date 2021-07-26 21:56
 */
public interface MessageContext {
    public ChannelHandlerContext getChannelHandlerContext();
}
