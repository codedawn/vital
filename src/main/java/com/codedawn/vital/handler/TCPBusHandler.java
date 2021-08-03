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
 * @date 2021-07-21 11:01
 */
public class TCPBusHandler extends ChannelInboundHandlerAdapter {


    private static Logger log = LoggerFactory.getLogger(TCPBusHandler.class);


    private Class<? extends Protocol> protocolClass;

    private ProtocolManager protocolManager;

    public TCPBusHandler(Class<? extends Protocol> protocolClass, ProtocolManager protocolManager) {
        this.protocolClass = protocolClass;
        this.protocolManager = protocolManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CommandHandler commandHandler = protocolManager.getProtocol(protocolClass.getSimpleName()).getCommandHandler();
        commandHandler.handle(new DefaultMessageContext(ctx),msg);
    }



}
