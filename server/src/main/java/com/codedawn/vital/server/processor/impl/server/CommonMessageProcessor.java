package com.codedawn.vital.server.processor.impl.server;

import com.codedawn.vital.server.connector.VitalSendHelper;
import com.codedawn.vital.server.context.DefaultMessageContext;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalProtobuf;
import com.codedawn.vital.server.qos.SendQos;
import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * 服务器处理一般消息的转发
 * @author codedawn
 * @date 2021-07-29 16:48
 */
public class CommonMessageProcessor implements Processor<DefaultMessageContext, VitalMessageWrapper> {
    private static Logger log = LoggerFactory.getLogger(CommonMessageProcessor.class);

    private ExecutorService executor;


    private ConnectionManage connectionManage;

    private SendQos sendQos;

    public CommonMessageProcessor(ConnectionManage connectionManage,SendQos sendQos) {
        this.connectionManage = connectionManage;
        this.sendQos = sendQos;
    }

    public CommonMessageProcessor(ExecutorService executor, ConnectionManage connectionManage,SendQos sendQos) {
        this.executor = executor;
        this.connectionManage = connectionManage;
        this.sendQos = sendQos;
    }

    @Override
    public void process(DefaultMessageContext defaultMessageContext, VitalMessageWrapper vitalMessageWrapper) {
        VitalProtobuf.Protocol message = vitalMessageWrapper.getMessage();
        VitalProtobuf.CommonMessage commonMessage = message.getCommonMessage();
        if (commonMessage == null) {
            log.warn("commonMessage为null");
        }else {
            Connection connection = connectionManage.get(commonMessage.getToId());
            //说明在线
            if (connection != null) {
                //转发
                VitalSendHelper.send(connection.getChannel(),vitalMessageWrapper,sendQos);
                log.debug("CommonMessageProcessor转发消息{}",message.toString());
            }else {
                log.info("用户{}不在线，消息{}将不转发",commonMessage.getToId(),message.toString());
            }

        }

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
        return null;
    }
}
