package com.codedawn.vital.client.processor.impl.client;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.client.connector.Sender;
import com.codedawn.vital.client.connector.TCPConnect;
import com.codedawn.vital.client.context.DefaultMessageContext;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalProtobuf;
import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionEventType;
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

    private Sender sender;

    public AuthSuccessProcessor(TCPConnect tcpConnect, Sender sender) {
        this.tcpConnect = tcpConnect;
        this.sender = sender;
    }

    public AuthSuccessProcessor() {
    }

    @Override
    public void process(com.codedawn.vital.client.context.DefaultMessageContext clientMessageContext, VitalMessageWrapper messageWrapper) {
        preProcess(clientMessageContext, messageWrapper);
        ChannelHandlerContext channelHandlerContext = clientMessageContext.getChannelHandlerContext();
        Channel channel = channelHandlerContext.channel();
        VitalProtobuf.Protocol message = messageWrapper.getMessage();
        VitalProtobuf.AuthSuccessMessage authSuccessMessage = message.getAuthSuccessMessage();

        //channel和connection关联
        new Connection(channel, ClientVitalGenericOption.ID.value());
        channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        //认证成功

        //发送缓存的消息
        sender.sendRetainMessage();

        //不应该出现这种情况，VitalGenericOption.ID修改要重新启动客户端
        if (ClientVitalGenericOption.ID.value()!=null&& ClientVitalGenericOption.ID.value().equals(authSuccessMessage.getId())) {

        }else {
            log.info("AuthSuccessMessage 认证成功的id和Info中的不一样");

        }
        afterProcess(clientMessageContext, messageWrapper);


    }

    @Override
    public Object preProcess(com.codedawn.vital.client.context.DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {

        return null;
    }

    @Override
    public void afterProcess(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {

    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

}
