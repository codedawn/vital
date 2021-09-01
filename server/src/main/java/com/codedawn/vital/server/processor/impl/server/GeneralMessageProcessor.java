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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 服务器处理一般消息的转发
 * @author codedawn
 * @date 2021-07-29 16:48
 */
public class GeneralMessageProcessor implements Processor<DefaultMessageContext, VitalMessageWrapper> {
    private static Logger log = LoggerFactory.getLogger(GeneralMessageProcessor.class);

    private ExecutorService executor;


    private ConnectionManage connectionManage;

    private SendQos sendQos;

    public GeneralMessageProcessor(ConnectionManage connectionManage, SendQos sendQos) {
        this.connectionManage = connectionManage;
        this.sendQos = sendQos;
    }

    public GeneralMessageProcessor(ExecutorService executor, ConnectionManage connectionManage, SendQos sendQos) {
        this.executor = executor;
        this.connectionManage = connectionManage;
        this.sendQos = sendQos;
    }

    @Override
    public void process(DefaultMessageContext defaultMessageContext, VitalMessageWrapper vitalMessageWrapper) {

        VitalProtobuf.Protocol message = vitalMessageWrapper.getProtocol();
        VitalProtobuf.TextMessage commonMessage = message.getTextMessage();
        List<String> idList = null;


        if (commonMessage.getIsGroup()){
            //群发
            idList = onGroup(defaultMessageContext, vitalMessageWrapper);
            if (idList == null||idList.size()==0) {
                log.info("发送群组消息时，发送id列表为空,群组消息{}将不转发",message.toString());
                return;
            }
            for (String id : idList) {
                Connection connection = connectionManage.get(id);
                //说明在线
                if (connection != null) {
                    //转发
                    VitalSendHelper.send(connection.getChannel(),vitalMessageWrapper,sendQos);
                    log.debug("CommonMessageProcessor转发消息{}",message.toString());
                }else {
                    log.info("用户{}不在线，群组消息{}将不转发",commonMessage.getToId(),message.toString());
                }
            }

        }else {
            //一对一发送
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


    public List<String> onGroup(DefaultMessageContext defaultMessageContext, VitalMessageWrapper vitalMessageWrapper) {

        VitalProtobuf.Protocol message = vitalMessageWrapper.getProtocol();
        VitalProtobuf.TextMessage commonMessage = message.getTextMessage();
        List<String> idList = new ArrayList();
        return idList;
    }





    @Override
    public ExecutorService getExecutor() {
        return null;
    }
}
