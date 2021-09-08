package com.codedawn.vital.client.handler;
import com.codedawn.vital.server.handler.TCPBusHandler;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.ProtocolManager;
import io.netty.channel.ChannelHandlerContext;

public class ClientTCPHandler extends TCPBusHandler{

    public ClientTCPHandler(Class<? extends Protocol> protocolClass, ProtocolManager protocolManager) {
        super(protocolClass, protocolManager);
    }

    /**
     * Calls {@link ChannelHandlerContext#fireExceptionCaught(Throwable)} to forward
     * to the next {@link ChannelHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }
}