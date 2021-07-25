package com.codedawn.vital.server.handler;

import com.codedawn.vital.server.command.CommandHandler;
import com.codedawn.vital.server.context.ServerContext;
import com.codedawn.vital.server.proto.ProtocolManager;
import com.codedawn.vital.server.proto.VitalProtobuf;
import com.codedawn.vital.server.proto.VitalProtocol;
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




    public AuthHandler() {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        VitalProtobuf.Protocol protocol = (VitalProtobuf.Protocol) msg;

        CommandHandler commandHandler = ProtocolManager.getProtocol(VitalProtocol.class.getSimpleName()).getCommandHandler();
        commandHandler.handle(new ServerContext(ctx),protocol);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
