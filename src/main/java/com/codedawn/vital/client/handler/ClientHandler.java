package com.codedawn.vital.client.handler;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author codedawn
 * @date 2021-06-21 22:50
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<VitalProtocol.Protocol> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("连接断了，需要重连");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();

    }




    @Override
    protected void channelRead0(ChannelHandlerContext ctx, VitalProtocol.Protocol msg) throws Exception {

    }
}
