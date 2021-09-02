package com.codedawn.vital.server.processor.impl.server;

import com.codedawn.vital.server.context.DefaultMessageContext;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;
import com.codedawn.vital.server.qos.SendQos;
import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionEventType;
import com.codedawn.vital.server.session.ConnectionManage;
import com.codedawn.vital.server.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * 服务的处理认证的处理器
 * @author codedawn
 * @date 2021-07-25 22:01
 */
public class AuthProcessor implements Processor<DefaultMessageContext,VitalMessageWrapper> {

    private static Logger log = LoggerFactory.getLogger(AuthProcessor.class);

    private ExecutorService executor;


    private ConnectionManage connectionManage;

    private SendQos sendQos;

    private Protocol<VitalPB.Protocol> protocol;

    public AuthProcessor(ConnectionManage connectionManage,SendQos sendQos) {
        this.connectionManage = connectionManage;
        this.sendQos = sendQos;
    }

    public AuthProcessor(ExecutorService executor, ConnectionManage connectionManage,SendQos sendQos) {
        this.executor = executor;
        this.connectionManage = connectionManage;
        this.sendQos = sendQos;
    }

    @Override
    public void process(DefaultMessageContext defaultMessageContext, VitalMessageWrapper vitalMessageWrapper) {

        VitalPB.AuthRequestMessage authRequestMessage = vitalMessageWrapper.getMessage();
        if (authRequestMessage == null || StringUtils.isEmpty(authRequestMessage.getId())) {
            log.warn("AuthMessage没有设置id，这是不允许的，这将是channel的唯一标识");
            return;
        }

        if(onAuth(defaultMessageContext, authRequestMessage)){
            createConnection(defaultMessageContext,authRequestMessage.getId());
        }
        else {
            connectFailed(defaultMessageContext,vitalMessageWrapper);
        }
    }


    /**
     * 重写该方法，实现认证逻辑
     * @param defaultMessageContext
     * @param authRequestMessage
     * @return 认证成功返回true，否则返回false
     */
    public boolean onAuth(DefaultMessageContext defaultMessageContext, VitalPB.AuthRequestMessage authRequestMessage) {
        //重写这个方法实现自己的登录逻辑
        //        如果认证成功

        //        如果认证失败
        //        connectFailed(defaultMessageContext,vitalMessageWrapper.getQosId(),"失败描述");
        return true;
    }



    /**
     * 认证成功请调用该方法
     * @param defaultMessageContext
     * @param id
     */
    public void createConnection(DefaultMessageContext defaultMessageContext, String id) {

        //移除authHandler
        ChannelHandlerContext channelHandlerContext = defaultMessageContext.getChannelHandlerContext();
        channelHandlerContext.pipeline().remove("AuthHandler");

        if (channelHandlerContext.channel().isActive()) {
            //绑定connection到channel
            new Connection(channelHandlerContext.channel(),id);
            //触发CONNECT事件
            channelHandlerContext.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        }else {
            //触发CONNECT_FAILED事件
            channelHandlerContext.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT_FAILED);
        }

        //发送认证成功的消息
        VitalPB.Protocol authSuccess = protocol.createAuthSuccess(id);
        protocol.send(channelHandlerContext.channel(),authSuccess);

    }


    /**
     * 认证失败请调用该方法
     * @param defaultMessageContext
     *
     */
    public void connectFailed(DefaultMessageContext defaultMessageContext,VitalMessageWrapper vitalMessageWrapper) {
        //发送CONNECT_FAILED事件
        ChannelHandlerContext channelHandlerContext = defaultMessageContext.getChannelHandlerContext();

        channelHandlerContext.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT_FAILED);
//        VitalProtobuf.Protocol exception = VitalMessageFactory.createException(qosId, ErrorCode.AUTH_FAILED.getExtra(),ErrorCode.AUTH_FAILED.getCode());
//        VitalSendHelper.send(channelHandlerContext.channel(),exception,sendQos);
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }


    public AuthProcessor setExecutor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public Protocol<VitalPB.Protocol> getProtocol() {
        return protocol;
    }

    public AuthProcessor setProtocol(Protocol<VitalPB.Protocol> protocol) {
        this.protocol = protocol;
        return this;
    }
}