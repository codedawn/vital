package com.codedawn.vital.server.handler;

import com.codedawn.vital.command.CommandHandler;
import com.codedawn.vital.server.context.ServerMessageContext;
import com.codedawn.vital.proto.Protocol;
import com.codedawn.vital.proto.ProtocolManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-22 23:32
 */
@ChannelHandler.Sharable
public class AuthHandler extends ChannelInboundHandlerAdapter {

    private static Logger  log = LoggerFactory.getLogger(AuthHandler.class);

    private Class<? extends Protocol> protocolClass;

    private ProtocolManager protocolManager;


    public AuthHandler(Class<? extends Protocol> protocolClass,ProtocolManager protocolManager) {
        this.protocolClass = protocolClass;
        this.protocolManager = protocolManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        CommandHandler commandHandler = protocolManager.getProtocol(protocolClass.getSimpleName()).getCommandHandler();
        commandHandler.handle(new ServerMessageContext(ctx),msg);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("{}被移除",this.toString());
    }


}
