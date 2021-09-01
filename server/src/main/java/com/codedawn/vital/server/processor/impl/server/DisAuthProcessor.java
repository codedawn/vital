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
import com.codedawn.vital.server.session.ConnectionManage;
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
        VitalProtobuf.Protocol p =  vitalMessageWrapper.getProtocol();
        VitalProtobuf.DisAuthMessage disAuthMessage = p.getDisAuthMessage();
        ChannelHandlerContext channelHandlerContext = defaultMessageContext.getChannelHandlerContext();

        if (disAuthMessage != null) {
            if (StringUtils.isEmpty(disAuthMessage.getId())) {
                log.warn("disAuthMessage消息中id为空");
            }else {
                Connection connection = channelHandlerContext.channel().attr(Connection.CONNECTION).get();
                if(connection.getId().equals(disAuthMessage.getId())){
                    //发送解除认证成功的消息
                    VitalProtobuf.Protocol disAuthFinish = VitalMessageFactory.createDisAuthFinish(disAuthMessage.getId());
                    VitalSendHelper.send(channelHandlerContext.channel(),disAuthFinish,sendQos);
                    //移除该connection
//                    connectionManage.remove(connection);
                    //断开连接
                    channelHandlerContext.channel().close();
                }else {
                    log.warn("非法disAuthMessage消息，当前connection中的id，与disAuthMessage消息中的id不同");
                    VitalProtobuf.Protocol exception = VitalMessageFactory.createException(vitalMessageWrapper.getSeq(), ErrorCode.ILLEGAL_DISAUTHMESSAGE.getExtra(),ErrorCode.ILLEGAL_DISAUTHMESSAGE.getCode());
                    VitalSendHelper.send(channelHandlerContext.channel(), exception, sendQos);
                }
            }
        }

    }



    @Override
    public ExecutorService getExecutor() {
        return null;
    }
}
