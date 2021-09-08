package com.codedawn.vital.server.handler;

import com.codedawn.vital.server.command.CommandHandler;
import com.codedawn.vital.server.context.DefaultMessageContext;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.ProtocolManager;
import com.codedawn.vital.server.proto.VitalPB;
import com.codedawn.vital.server.proto.VitalProtocol;
import com.codedawn.vital.server.util.AddressUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


        if (protocolClass == VitalProtocol.class) {
            if (!checkPermit(msg,ctx)) {
                //不放行
                return;
            }
        }
        CommandHandler commandHandler = protocolManager.getProtocol(protocolClass.getSimpleName()).getCommandHandler();
        commandHandler.handle(new DefaultMessageContext(ctx),msg);

    }

    /**
     *  查看消息是否可以通行，没认证只能放行，auth和heartbeat和ack
     */
    private boolean checkPermit(Object msg,ChannelHandlerContext ctx) {
        if (msg instanceof VitalPB.Frame) {
            VitalPB.Frame protocol= (VitalPB.Frame) msg;
            //没有设置头或body
            if(!protocol.hasHeader()||!protocol.hasBody()){
                //心跳
                log.info("AuthHandler中心跳消息遭到丢弃,心跳检测来自：{}",ctx.channel().remoteAddress());
                return false;
            }
            VitalPB.Body body = ((VitalPB.Frame) msg).getBody();
            //没有设置oneof
            if(!body.hasOneof(VitalPB.Body.getDescriptor().getOneofs().get(0))){
                log.info("AuthHandler中消息遭到丢弃,没有设置消息体来自：{}",ctx.channel().remoteAddress());
                return false;
            }else {
                if(!body.getMessageType().name().equals(body.getOneofFieldDescriptor(VitalPB.Body.getDescriptor().getOneofs().get(0)).getMessageType().getFullName()+"Type")){
                    log.info("AuthHandler中消息遭到丢弃,消息体异常来自：{}",ctx.channel().remoteAddress());
                    return false;
                }

            }
            //认证消息或ack消息放行
            VitalPB.MessageType dataType = body.getMessageType();
            if(dataType==VitalPB.MessageType.AuthRequestMessageType||dataType== VitalPB.MessageType.AckMessageType){
                return true;
            }else {
                log.info("未进行认证，不能发送非认证消息");
                return false;
            }
        }
        log.info("AuthHandler中消息遭到丢弃,不合法消息来自：{}",ctx.channel().remoteAddress());
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
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("authHandler：{}被移除",this.toString());
    }


}
