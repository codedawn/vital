package com.codedawn.vital.server.processor.impl.server;

import com.codedawn.vital.server.callback.ErrorCode;
import com.codedawn.vital.server.context.DefaultMessageContext;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;
import com.codedawn.vital.server.qos.SendQos;
import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionManage;
import com.codedawn.vital.server.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * 解除认证processor，服务器使用
 * @author codedawn
 * @date 2021-07-29 14:29
 */
public class DisAuthProcessor implements Processor<DefaultMessageContext, VitalMessageWrapper> {

    private static Logger log = LoggerFactory.getLogger(DisAuthProcessor.class);

    private ExecutorService executor;


    private ConnectionManage connectionManage;

    private SendQos sendQos;

    private Protocol<VitalPB.Protocol> protocol;


    public DisAuthProcessor(ConnectionManage connectionManage, SendQos sendQos) {
        this.connectionManage = connectionManage;
        this.sendQos = sendQos;
    }

    public DisAuthProcessor(ExecutorService executor, ConnectionManage connectionManage, SendQos sendQos) {
        this.executor = executor;
        this.connectionManage = connectionManage;
        this.sendQos = sendQos;
    }

    @Override
    public void process(DefaultMessageContext defaultMessageContext, VitalMessageWrapper vitalMessageWrapper) {

        VitalPB.DisAuthMessage disAuthMessage = vitalMessageWrapper.getMessage();
        ChannelHandlerContext channelHandlerContext = defaultMessageContext.getChannelHandlerContext();

        if (disAuthMessage != null) {
            if (StringUtils.isEmpty(disAuthMessage.getId())) {
                log.warn("disAuthMessage消息中id为空");
            }else {
                Connection connection = channelHandlerContext.channel().attr(Connection.CONNECTION).get();
                if(connection.getId().equals(disAuthMessage.getId())){
                    //发送解除认证成功的消息
                    VitalPB.Protocol disAuthFinish = protocol.createDisAuthSuccess(disAuthMessage.getId());
                    protocol.send(channelHandlerContext.channel(),disAuthFinish);

                    //断开连接
                    channelHandlerContext.channel().close();
                }else {
                    log.warn("非法disAuthMessage消息，当前connection中的id，与disAuthMessage消息中的id不同");
                    VitalPB.Protocol exception = protocol.createException(vitalMessageWrapper.getSeq(), ErrorCode.ILLEGAL_DISAUTHMESSAGE.getExtra(),ErrorCode.ILLEGAL_DISAUTHMESSAGE.getCode());
                    protocol.send(channelHandlerContext.channel(), exception);
                }
            }
        }

    }


    public Protocol<VitalPB.Protocol> getProtocol() {
        return protocol;
    }

    public DisAuthProcessor setProtocol(Protocol<VitalPB.Protocol> protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    public ExecutorService getExecutor() {
        return null;
    }
}