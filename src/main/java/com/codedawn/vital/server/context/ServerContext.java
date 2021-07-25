package com.codedawn.vital.server.context;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author codedawn
 * @date 2021-07-24 23:05
 */
public class ServerContext {
    private ChannelHandlerContext channelHandlerContext;


    public ServerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;

    }
}
