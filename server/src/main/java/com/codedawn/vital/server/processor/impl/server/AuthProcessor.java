package com.codedawn.vital.server.processor.impl.server;

import com.codedawn.vital.server.callback.ErrorCode;
import com.codedawn.vital.server.connector.VitalSendHelper;
import com.codedawn.vital.server.context.DefaultMessageContext;
import com.codedawn.vital.server.factory.VitalMessageFactory;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalProtobuf;
import com.codedawn.vital.server.qos.SendQos;
import com.codedawn.vital.server.util.StringUtils;
import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionEventType;
import com.codedawn.vital.server.session.ConnectionManage;
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

        VitalProtobuf.Protocol p =  vitalMessageWrapper.getProtocol();
        VitalProtobuf.AuthMessage authMessage = p.getAuthMessage();

        if (authMessage == null || StringUtils.isEmpty(p.getAuthMessage().getId())) {
            log.warn("AuthMessage没有设置id，这是不允许的，这将是channel的唯一标识");

        }
        onAuth(defaultMessageContext, vitalMessageWrapper);

    }



    public void onAuth(DefaultMessageContext defaultMessageContext, VitalMessageWrapper vitalMessageWrapper) {
        //重写这个方法实现自己的登录逻辑
        VitalProtobuf.AuthMessage authMessage = vitalMessageWrapper.getProtocol().getAuthMessage();
        //        如果认证成功
        createConnection(defaultMessageContext,authMessage.getId());
        //        如果认证失败
        //        connectFailed(defaultMessageContext,vitalMessageWrapper.getQosId(),"失败描述");
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
        VitalProtobuf.Protocol authSuccess = VitalMessageFactory.createAuthSuccess(id);
        VitalSendHelper.send(channelHandlerContext.channel(),authSuccess,sendQos);

    }


    /**
     * 认证失败请调用该方法
     * @param defaultMessageContext
     * @param qosId
     * @param extra 失败描述
     */
    public void connectFailed(DefaultMessageContext defaultMessageContext,String qosId,String extra) {
        //发送CONNECT_FAILED事件
        ChannelHandlerContext channelHandlerContext = defaultMessageContext.getChannelHandlerContext();

        channelHandlerContext.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT_FAILED);
        VitalProtobuf.Protocol exception = VitalMessageFactory.createException(qosId, ErrorCode.AUTH_FAILED.getExtra(),ErrorCode.AUTH_FAILED.getCode());
        VitalSendHelper.send(channelHandlerContext.channel(),exception,sendQos);
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }


    public AuthProcessor setExecutor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }
}
