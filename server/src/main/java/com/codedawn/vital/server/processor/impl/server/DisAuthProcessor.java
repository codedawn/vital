package com.codedawn.vital.server.processor.impl.server;

import com.codedawn.vital.server.context.MessageContext;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.VitalPB;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * 解除认证processor，服务器使用
 * @author codedawn
 * @date 2021-07-29 14:29
 */
public class DisAuthProcessor implements Processor<MessageContext, MessageWrapper> {

    private static Logger log = LoggerFactory.getLogger(DisAuthProcessor.class);

    private ExecutorService executor;



    private Protocol<VitalPB.Frame> protocol;


    public DisAuthProcessor() {
    }

    public DisAuthProcessor(ExecutorService executor, Protocol<VitalPB.Frame> protocol) {
        this.executor = executor;
        this.protocol = protocol;
    }

    @Override
    public void process(MessageContext messageContext, MessageWrapper messageWrapper) {

        ChannelHandlerContext channelHandlerContext = messageContext.getChannelHandlerContext();
        //断开连接
        channelHandlerContext.channel().close();

    }


    public Protocol<VitalPB.Frame> getProtocol() {
        return protocol;
    }

    public DisAuthProcessor setProtocol(Protocol<VitalPB.Frame> protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    public ExecutorService getExecutor() {
        return null;
    }
}
