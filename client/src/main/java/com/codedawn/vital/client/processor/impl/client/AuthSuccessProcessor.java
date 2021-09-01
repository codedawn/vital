package com.codedawn.vital.client.processor.impl.client;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.client.context.DefaultMessageContext;
import com.codedawn.vital.server.callback.AuthResponseCallBack;
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

    private AuthResponseCallBack authResponseCallBack;

    public AuthSuccessProcessor() {
    }


    @Override
    public void process(DefaultMessageContext clientMessageContext, VitalMessageWrapper messageWrapper) {
        ChannelHandlerContext channelHandlerContext = clientMessageContext.getChannelHandlerContext();
        Channel channel = channelHandlerContext.channel();
        VitalProtobuf.Protocol message = messageWrapper.getProtocol();
        VitalProtobuf.AuthSuccessMessage authSuccessMessage = message.getAuthSuccessMessage();

        //channel和connection关联
        new Connection(channel, ClientVitalGenericOption.ID.value());
        channel.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        //认证成功

        if (authResponseCallBack != null) {
            authResponseCallBack.success(messageWrapper);
        }

        //不应该出现这种情况，VitalGenericOption.ID修改要重新启动客户端
        if (ClientVitalGenericOption.ID.value()!=null&& ClientVitalGenericOption.ID.value().equals(authSuccessMessage.getId())) {

        }else {
            log.info("AuthSuccessMessage 认证成功的id和Info中的不一样");

        }



    }



    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    public AuthSuccessProcessor setAuthResponseCallBack(AuthResponseCallBack authResponseCallBack) {
        this.authResponseCallBack = authResponseCallBack;
        return this;
    }
}
