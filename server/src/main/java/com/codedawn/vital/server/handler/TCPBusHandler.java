package com.codedawn.vital.server.handler;

import com.codedawn.vital.server.command.CommandHandler;
import com.codedawn.vital.server.context.DefaultMessageContext;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.ProtocolManager;
import com.codedawn.vital.server.proto.VitalProtobuf;
import com.codedawn.vital.server.proto.VitalTCPProtocol;
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
        if (protocolClass == VitalTCPProtocol.class) {
            if (!checkPermit(msg)) {
                //不放行
                log.info("已认证，不能重复认证");
                return;
            }
        }
        CommandHandler commandHandler = protocolManager.getProtocol(protocolClass.getSimpleName()).getCommandHandler();
        commandHandler.handle(new DefaultMessageContext(ctx),msg);
    }
    /**
     *  查看消息是否可以通行，不处理auth，因为已经认证过了
     */
    private boolean checkPermit(Object msg) {
        if (msg instanceof VitalProtobuf.Protocol) {
            VitalProtobuf.DataType dataType = ((VitalProtobuf.Protocol) msg).getDataType();
            if (dataType == VitalProtobuf.DataType.AuthMessageType) {
                return false;
            }
        }
        return true;
    }


}
