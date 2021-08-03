package com.codedawn.vital.processor.impl.client;

import com.codedawn.vital.connector.TCPConnect;
import com.codedawn.vital.context.DefaultMessageContext;
import com.codedawn.vital.processor.Processor;
import com.codedawn.vital.proto.VitalMessageWrapper;
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

    public DisAuthFinishProcessor(TCPConnect tcpConnect) {
        this.tcpConnect = tcpConnect;
    }

    public DisAuthFinishProcessor(ExecutorService executor, TCPConnect tcpConnect) {
        this.executor = executor;
        this.tcpConnect = tcpConnect;
    }

    @Override
    public void process(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {
        preProcess(messageContext, messageWrapper);
        ChannelHandlerContext channelHandlerContext = messageContext.getChannelHandlerContext();
        channelHandlerContext.channel().close();
        tcpConnect.setConnect(false);
        afterProcess(messageContext,messageWrapper);
    }

    @Override
    public void preProcess(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {

    }

    @Override
    public void afterProcess(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {

    }

    @Override
    public ExecutorService getExecutor() {
        return null;
    }
}
