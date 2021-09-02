package com.codedawn.vital.server.handler;

import com.codedawn.vital.server.command.CommandHandler;
import com.codedawn.vital.server.context.DefaultMessageContext;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.ProtocolManager;
import com.codedawn.vital.server.proto.VitalPB;
import com.codedawn.vital.server.proto.VitalProtocol;
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


    private Class<? extends Protocol> protocolClass;

    private ProtocolManager protocolManager;

    public TCPBusHandler(Class<? extends Protocol> protocolClass, ProtocolManager protocolManager) {
        this.protocolClass = protocolClass;
        this.protocolManager = protocolManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (protocolClass == VitalProtocol.class) {
            if (!checkPermit(ctx,msg)) {
                //不放行
                log.info("TCPBusHandler中消息遭到丢弃，不是合法消息，或者已认证，不能重复认证");
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
        if (msg instanceof VitalPB.Protocol) {
            VitalPB.Protocol protocol= (VitalPB.Protocol) msg;
            //没有设置头或body
            if(!protocol.hasHeader()||!protocol.hasBody()){
                //心跳
                return false;
            }
            VitalPB.Body body = ((VitalPB.Protocol) msg).getBody();
            //没有设置oneof
            if(!body.hasOneof(VitalPB.Body.getDescriptor().getOneofs().get(0))){
//                log.info("来自{}的心跳", AddressUtil.parseRemoteAddress(ctx.channel()));
                return false;
            }
            //认证消息不放行
            VitalPB.MessageType dataType = body.getMessageType();
            return dataType != VitalPB.MessageType.AuthRequestMessageType;
        }
        return false;
    }


}
