package com.codedawn.vital.server.handler;

import com.codedawn.vital.server.command.CommandHandler;
import com.codedawn.vital.server.context.ServerContext;
import com.codedawn.vital.server.processor.UserProcessorManager;
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


    private CommandHandler commandHandler;

    private UserProcessorManager userProcessorManager;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        VitalProtocol.Protocol protocol = (VitalProtocol.Protocol) msg;

        commandHandler.handle(new ServerContext(ctx,userProcessorManager),protocol);
    }



}
