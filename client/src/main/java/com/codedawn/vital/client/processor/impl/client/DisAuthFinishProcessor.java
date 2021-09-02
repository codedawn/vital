package com.codedawn.vital.client.processor.impl.client;

import com.codedawn.vital.client.connector.TCPConnect;
import com.codedawn.vital.server.context.DefaultMessageContext;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * 收到解除连接成功的消息使用该处理器,使用于客户端
 * @author codedawn
 * @date 2021-07-31 11:02
 */
public class DisAuthFinishProcessor implements Processor<DefaultMessageContext, VitalMessageWrapper> {

    private static Logger log = LoggerFactory.getLogger(DisAuthFinishProcessor.class);

    private ExecutorService executor;


    private TCPConnect tcpConnect;

    public DisAuthFinishProcessor() {
    }

    @Override
    public void process(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {
        ChannelHandlerContext channelHandlerContext = messageContext.getChannelHandlerContext();
        channelHandlerContext.channel().close();
        //主动断开
        tcpConnect.setConnect(false);

    }



    @Override
    public ExecutorService getExecutor() {
        return null;
    }


    public DisAuthFinishProcessor setTcpConnect(TCPConnect tcpConnect) {
        this.tcpConnect = tcpConnect;
        return this;
    }
}
