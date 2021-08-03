package com.codedawn.vital.handler;


import com.codedawn.vital.command.CommandHandler;
import com.codedawn.vital.context.DefaultMessageContext;
import com.codedawn.vital.proto.Protocol;
import com.codedawn.vital.proto.ProtocolManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-06-21 22:50
 */

public class TCPClientHandler extends ChannelInboundHandlerAdapter {

    private static Logger log = LoggerFactory.getLogger(TCPClientHandler.class);

    private Protocol protocol;


    private ProtocolManager protocolManager;

    public TCPClientHandler(Protocol protocol,ProtocolManager protocolManager) {
        this.protocol = protocol;
        this.protocolManager = protocolManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CommandHandler commandHandler = protocolManager.getProtocol(protocol.getClass().getSimpleName()).getCommandHandler();
        commandHandler.handle(new DefaultMessageContext(ctx),msg);
    }
}
