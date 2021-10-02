package com.codedawn.vital.server.handler;

import com.codedawn.vital.server.command.CommandHandler;
import com.codedawn.vital.server.context.DefaultMessageContext;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.ProtocolManager;
import com.codedawn.vital.server.proto.VitalPB;
import com.codedawn.vital.server.proto.VitalProtocol;
import com.codedawn.vital.server.session.Connection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读消息
 * @author codedawn
 * @date 2021-07-21 11:01
 */
public class TCPBusHandler extends ChannelInboundHandlerAdapter {


    private static Logger log = LoggerFactory.getLogger(TCPBusHandler.class);


    protected Class<? extends Protocol> protocolClass;

    protected ProtocolManager protocolManager;

    public TCPBusHandler(Class<? extends Protocol> protocolClass, ProtocolManager protocolManager) {
        this.protocolClass = protocolClass;
        this.protocolManager = protocolManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (protocolClass == VitalProtocol.class) {
            if (!checkPermit(ctx,msg)) {
                //不放行
//                log.info("TCPBusHandler中消息遭到丢弃，不是合法消息");
                return;
            }

        }
        CommandHandler commandHandler = protocolManager.getProtocol(protocolClass.getSimpleName()).getCommandHandler();
        commandHandler.handle(new DefaultMessageContext(ctx),msg);
    }

    /**
     * 查看消息是否可以通行，不处理auth，因为已经认证过了
     * @param msg
     * @return 通行返回true，否则返回false
     */
    private boolean checkPermit(ChannelHandlerContext ctx,Object msg) {
        if (msg instanceof VitalPB.Frame) {
            VitalPB.Frame protocol= (VitalPB.Frame) msg;
            //没有设置头或body
            if(!protocol.hasHeader()||!protocol.hasBody()){
                //心跳
                log.info("TCPBusHandler中心跳消息遭到丢弃,心跳检测来自：{}",ctx.channel().attr(Connection.CONNECTION).get().getId());
                return false;
            }
            VitalPB.Body body = ((VitalPB.Frame) msg).getBody();
            //没有设置oneof
            if(!body.hasOneof(VitalPB.Body.getDescriptor().getOneofs().get(0))){
                log.info("TCPBusHandler中消息遭到丢弃,没有设置消息体来自：{}",ctx.channel().attr(Connection.CONNECTION).get().getId());
                return false;
            }else {
                if(!body.getMessageType().name().equals(body.getOneofFieldDescriptor(VitalPB.Body.getDescriptor().getOneofs().get(0)).getMessageType().getFullName()+"Type")){
                    log.info("TCPBusHandler中消息遭到丢弃,消息体异常来自：{}",ctx.channel().attr(Connection.CONNECTION).get().getId());
                    return false;
                }

            }

            //认证消息不放行
            VitalPB.MessageType dataType = body.getMessageType();
            if(dataType==VitalPB.MessageType.AuthRequestMessageType){
                log.info("TCPBusHandler中认证消息遭到丢弃,不能重复认证来自：{}",ctx.channel().attr(Connection.CONNECTION).get().getId());
                return false;
            }

            //消息的fromId和认证id不一致，属于非法消息
            if(!checkFromId(ctx,protocol)){
                log.warn("消息的fromId和认证id不一致，属于非法消息");
                return false;
            }
            return true;
        }
        log.info("TCPBusHandler中消息遭到丢弃,不合法消息来自：{}",ctx.channel().attr(Connection.CONNECTION).get().getId());
        return false;
    }


    private boolean checkFromId(ChannelHandlerContext ctx, VitalPB.Frame frame){
        if("".equals(frame.getHeader().getFromId())){
            return true;
        }
        return ctx.channel().attr(Connection.CONNECTION).get().getId().equals(frame.getHeader().getFromId());
    }

}
