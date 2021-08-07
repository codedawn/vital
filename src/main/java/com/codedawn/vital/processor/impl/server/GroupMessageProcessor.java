package com.codedawn.vital.processor.impl.server;

import com.codedawn.vital.connector.VitalSendHelper;
import com.codedawn.vital.context.DefaultMessageContext;
import com.codedawn.vital.processor.Processor;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.qos.SendQos;
import com.codedawn.vital.session.Connection;
import com.codedawn.vital.session.ConnectionManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author codedawn
 * @date 2021-08-07 10:05
 */
public class GroupMessageProcessor implements Processor<DefaultMessageContext, VitalMessageWrapper> {
    private static Logger log = LoggerFactory.getLogger(GroupMessageProcessor.class);

    private ExecutorService executor=null;


    private ConnectionManage connectionManage;

    private SendQos sendQos;

    public GroupMessageProcessor(ConnectionManage connectionManage, SendQos sendQos) {
        this.connectionManage = connectionManage;
        this.sendQos = sendQos;
    }

    public GroupMessageProcessor(ExecutorService executor, ConnectionManage connectionManage, SendQos sendQos) {
        this.executor = executor;
        this.connectionManage = connectionManage;
        this.sendQos = sendQos;
    }

    @Override
    public void process(DefaultMessageContext defaultMessageContext, VitalMessageWrapper vitalMessageWrapper) {
        VitalProtobuf.Protocol message = vitalMessageWrapper.getMessage();
        VitalProtobuf.GroupMessage groupMessage = message.getGroupMessage();
        if (groupMessage == null) {
            log.warn("groupMessage为null");
        }else {
            List<String> idList = preProcess(defaultMessageContext, vitalMessageWrapper);
            if (idList == null) {
                log.warn("idList为null");
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
                    log.debug("用户{}不在线，消息{}将不转发",groupMessage.getToId(),message.toString());
                }
            }


        }
    }

    @Override
    public List<String> preProcess(DefaultMessageContext defaultMessageContext, VitalMessageWrapper vitalMessageWrapper) {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("333");
        list.add("444");
        return list;
    }

    @Override
    public void afterProcess(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {

    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }
}
