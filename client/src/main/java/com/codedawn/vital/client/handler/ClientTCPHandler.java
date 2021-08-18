package com.codedawn.vital.client.handler;


import com.codedawn.vital.client.context.DefaultMessageContext;
import com.codedawn.vital.server.command.CommandHandler;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.ProtocolManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * client的读消息Handler
 * @author codedawn
 * @date 2021-06-21 22:50
 */

public class ClientTCPHandler extends ChannelInboundHandlerAdapter {

    private static Logger log = LoggerFactory.getLogger(ClientTCPHandler.class);

    private Protocol protocol;


    private ProtocolManager protocolManager;

    public ClientTCPHandler(Protocol protocol, ProtocolManager protocolManager) {
        this.protocol = protocol;
        this.protocolManager = protocolManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CommandHandler commandHandler = protocolManager.getProtocol(protocol.getClass().getSimpleName()).getCommandHandler();
        commandHandler.handle(new DefaultMessageContext(ctx),msg);
    }
}
