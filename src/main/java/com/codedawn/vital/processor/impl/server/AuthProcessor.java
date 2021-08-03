package com.codedawn.vital.processor.impl.server;

import com.codedawn.vital.connector.VitalSendHelper;
import com.codedawn.vital.context.DefaultMessageContext;
import com.codedawn.vital.factory.VitalMessageFactory;
import com.codedawn.vital.processor.Processor;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.qos.SendQos;
import com.codedawn.vital.session.Connection;
import com.codedawn.vital.session.ConnectionEventType;
import com.codedawn.vital.session.ConnectionManage;
import com.codedawn.vital.util.StringUtils;
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
        preProcess(defaultMessageContext, vitalMessageWrapper);
        VitalProtobuf.Protocol p =  vitalMessageWrapper.getMessage();
        VitalProtobuf.AuthMessage authMessage = p.getAuthMessage();

        if (authMessage == null || StringUtils.isEmpty(p.getAuthMessage().getId())) {
            log.warn("AuthMessage没有设置id，这是不允许的，这将是channel的唯一标识");
            return;
        }
        afterProcess(defaultMessageContext, vitalMessageWrapper);
    }

    @Override
    public void preProcess(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {

    }

    @Override
    public void afterProcess(DefaultMessageContext defaultMessageContext, VitalMessageWrapper vitalMessageWrapper) {
        //重写这个方法实现自己的登录逻辑
        VitalProtobuf.AuthMessage authMessage = vitalMessageWrapper.getMessage().getAuthMessage();
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
        channelHandlerContext.pipeline().remove(channelHandlerContext.handler());

        if (channelHandlerContext.channel().isActive()) {
            connectionManage.add(new Connection(channelHandlerContext.channel(),id));
            //发送CONNECT事件
            channelHandlerContext.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT);
        }else {
            //发送CONNECT_FAILED事件
            channelHandlerContext.pipeline().fireUserEventTriggered(ConnectionEventType.CONNECT_FAILED);
        }

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
        VitalProtobuf.Protocol exception = VitalMessageFactory.createException(qosId, extra);
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
