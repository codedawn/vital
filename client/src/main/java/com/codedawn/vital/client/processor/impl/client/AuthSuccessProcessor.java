package com.codedawn.vital.client.processor.impl.client;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.client.qos.ClientSendQos;
import com.codedawn.vital.server.context.MessageContext;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;
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
public class AuthSuccessProcessor implements Processor<MessageContext,MessageWrapper> {

    private static Logger log = LoggerFactory.getLogger(AuthSuccessProcessor.class);

    private ExecutorService executor;


    private ClientSendQos clientSendQos;

    public AuthSuccessProcessor() {
    }

    @Override
    public void process(MessageContext messageContext, MessageWrapper messageWrapper) {
        ChannelHandlerContext channelHandlerContext = messageContext.getChannelHandlerContext();
        Channel channel = channelHandlerContext.channel();

        VitalPB.AuthSuccessMessage authSuccessMessage=messageWrapper.getMessage();

        //channel和connection关联
        new Connection(channel, ClientVitalGenericOption.ID.value());
        channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        //认证成功

        clientSendQos.invokeResponseCallBack(authSuccessMessage.getAckSeq(),messageWrapper);

    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }



    public AuthSuccessProcessor setClientSendQos(ClientSendQos clientSendQos) {
        this.clientSendQos = clientSendQos;
        return this;
    }
}
