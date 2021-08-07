package com.codedawn.vital.handler;

import com.codedawn.vital.command.CommandHandler;
import com.codedawn.vital.proto.Protocol;
import com.codedawn.vital.proto.ProtocolManager;
import com.codedawn.vital.context.DefaultMessageContext;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.proto.VitalTCPProtocol;
import com.codedawn.vital.util.AddressUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 认证handler，所有channel共享一个
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

        InetSocketAddress inetSocketAddress = (InetSocketAddress) (ctx.channel().remoteAddress());
        System.out.println(inetSocketAddress.getAddress());



        if (protocolClass == VitalTCPProtocol.class) {
            if (!checkPermit(msg)) {
                //不放行
                log.info("未进行认证，不能发送非认证消息");
                return;
            }
        }
        CommandHandler commandHandler = protocolManager.getProtocol(protocolClass.getSimpleName()).getCommandHandler();
        commandHandler.handle(new DefaultMessageContext(ctx),msg);

    }

    /**
     *  查看消息是否可以通行，没认证只能放行，auth和heartbeat和ack
     */
    private boolean checkPermit(Object msg) {
        if (msg instanceof VitalProtobuf.Protocol) {
            VitalProtobuf.DataType dataType = ((VitalProtobuf.Protocol) msg).getDataType();
            if (dataType == VitalProtobuf.DataType.AuthMessageType
                    || dataType == VitalProtobuf.DataType.HeartbeatType
                    ||dataType== VitalProtobuf.DataType.AckMessageType
                    ||dataType== VitalProtobuf.DataType.AckMessageWithExtraType) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("authHandler中，未认证连接断开:{}", AddressUtil.parseRemoteAddress(ctx.channel()));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("authHandler中，未认证连接成功:{}",AddressUtil.parseRemoteAddress(ctx.channel()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("authHandler：{}被移除",this.toString());
    }


}
