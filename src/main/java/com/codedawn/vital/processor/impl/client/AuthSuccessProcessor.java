package com.codedawn.vital.processor.impl.client;

import com.codedawn.vital.config.VitalGenericOption;
import com.codedawn.vital.connector.TCPConnect;
import com.codedawn.vital.context.DefaultMessageContext;
import com.codedawn.vital.processor.Processor;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.session.Connection;
import com.codedawn.vital.session.ConnectionEventType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * 认证成功消息的处理器，一般用于客户端
 * @author codedawn
 * @date 2021-07-28 23:42
 */
public class AuthSuccessProcessor implements Processor<DefaultMessageContext, VitalMessageWrapper> {

    private static Logger log = LoggerFactory.getLogger(AuthSuccessProcessor.class);

    private ExecutorService executor;


    private TCPConnect tcpConnect;

    public AuthSuccessProcessor(TCPConnect tcpConnect) {
        this.tcpConnect = tcpConnect;
    }

    public AuthSuccessProcessor(ExecutorService executor, TCPConnect tcpConnect) {
        this.executor = executor;
        this.tcpConnect = tcpConnect;
    }

    public AuthSuccessProcessor() {
    }

    @Override
    public void process(DefaultMessageContext clientMessageContext, VitalMessageWrapper messageWrapper) {
        preProcess(clientMessageContext, messageWrapper);
        ChannelHandlerContext channelHandlerContext = clientMessageContext.getChannelHandlerContext();
        Channel channel = channelHandlerContext.channel();
        VitalProtobuf.Protocol message = messageWrapper.getMessage();
        VitalProtobuf.AuthSuccessMessage authSuccessMessage = message.getAuthSuccessMessage();

        //channel和connection关联
        new Connection(channel, VitalGenericOption.ID.value());
        channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        //认证成功
        tcpConnect.setAuth(true);

        //不应该出现这种情况，VitalGenericOption.ID修改要重新启动客户端
        if (VitalGenericOption.ID.value()!=null&&VitalGenericOption.ID.value().equals(authSuccessMessage.getId())) {

        }else {
            log.info("AuthSuccessMessage 认证成功的id和Info中的不一样");

        }
        afterProcess(clientMessageContext, messageWrapper);


    }

    @Override
    public Object preProcess(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {

        return null;
    }

    @Override
    public void afterProcess(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {

    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    public AuthSuccessProcessor setExecutor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }
}
