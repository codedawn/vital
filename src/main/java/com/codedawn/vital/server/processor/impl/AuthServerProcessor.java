package com.codedawn.vital.server.processor.impl;

import com.codedawn.vital.proto.MessageWrapper;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.server.context.ServerMessageContext;
import com.codedawn.vital.server.processor.ServerProcessor;
import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionManage;
import com.codedawn.vital.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author codedawn
 * @date 2021-07-25 22:01
 */
public class AuthServerProcessor implements ServerProcessor {

    private static Logger log = LoggerFactory.getLogger(AuthServerProcessor.class);

    private ExecutorService executor;


    private ConnectionManage connectionManage;

    public AuthServerProcessor(ConnectionManage connectionManage) {
        this.connectionManage = connectionManage;
    }

    @Override
    public void process(ServerMessageContext serverMessageContext, MessageWrapper messageWrapper) {
        VitalMessageWrapper vitalProtocolWrapper = (VitalMessageWrapper) messageWrapper;
        VitalProtobuf.Protocol p =  vitalProtocolWrapper.getMessage();
        VitalProtobuf.AuthMessage authMessage = p.getAuthMessage();

        if (authMessage == null || StringUtils.isEmpty(p.getAuthMessage().getId())) {
            log.warn("AuthMessage没有设置id，这是不允许的，这将是channel的唯一标识");
            return;
        }
        verify(serverMessageContext,authMessage);

    }

    public void verify(ServerMessageContext serverMessageContext, VitalProtobuf.AuthMessage authMessage) {
        //重写这个方法实现自己的登录逻辑


        createConnection(serverMessageContext,authMessage.getId());
    }

    /**
     * 登录成功
     * @param serverMessageContext
     * @param id
     */
    public void createConnection(ServerMessageContext serverMessageContext,String id) {

        //移除authHandler
        ChannelHandlerContext channelHandlerContext = serverMessageContext.getChannelHandlerContext();
        channelHandlerContext.pipeline().remove(channelHandlerContext.handler());


        connectionManage.add(new Connection(channelHandlerContext.channel(),id));
    }


    @Override
    public ExecutorService getExecutor() {
        return executor;
    }




}
