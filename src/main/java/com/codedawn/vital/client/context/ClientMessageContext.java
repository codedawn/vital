package com.codedawn.vital.client.context;

import com.codedawn.vital.context.MessageContext;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author codedawn
 * @date 2021-07-26 21:43
 */
public class ClientMessageContext implements MessageContext {
    private ChannelHandlerContext channelHandlerContext;


    public ClientMessageContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;

    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
}
