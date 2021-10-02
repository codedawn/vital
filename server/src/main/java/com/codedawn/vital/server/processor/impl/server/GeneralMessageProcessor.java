package com.codedawn.vital.server.processor.impl.server;

import com.codedawn.vital.server.context.MessageContext;
import com.codedawn.vital.server.logic.TransmitLogic;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.VitalPB;
import com.codedawn.vital.server.qos.SendQos;
import com.codedawn.vital.server.session.ConnectionManage;
import com.codedawn.vital.server.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 服务器处理一般消息的转发
 * @author codedawn
 * @date 2021-07-29 16:48
 */
public class GeneralMessageProcessor implements Processor<MessageContext, MessageWrapper> {
    private static Logger log = LoggerFactory.getLogger(GeneralMessageProcessor.class);

    private ExecutorService executor;


    private Protocol<VitalPB.Frame> protocol;

    private TransmitLogic transmitLogic;

    public GeneralMessageProcessor(ExecutorService executor, ConnectionManage connectionManage, SendQos sendQos) {
        this.executor = executor;
    }

    public GeneralMessageProcessor() {

    }




    @Override
    public void process(MessageContext messageContext, MessageWrapper messageWrapper) {

        List<String> idList;
        if (messageWrapper.getIsGroup()){
            //群发
            idList = transmitLogic.onGroup(messageContext, messageWrapper.getToId());
            String fromId = messageWrapper.getFromId();
            if (idList == null||idList.size()==0) {
                log.info("发送群组消息时，发送id列表为空,seq:{}群组消息将不转发",messageWrapper.getSeq());
                return;
            }
            for (String id : idList) {
                if(fromId.equals(id))continue;
                log.info("即将发送群组消息seq:{}",messageWrapper.getSeq());
                //转发
                protocol.send(id,messageWrapper);
            }

        }else {
            String id = transmitLogic.onOne(messageContext, messageWrapper.getToId());
            if(StringUtils.isEmpty(id)){
                log.info("发送单聊消息时，发送id为空,seq:{}单聊消息将不转发",messageWrapper.getSeq());
                return;
            }
            log.info("即将发送单聊消息seq:{}",messageWrapper.getSeq());
            //转发
            protocol.send(id,messageWrapper);

        }


    }





    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    public Protocol<VitalPB.Frame> getProtocol() {
        return protocol;
    }

    public GeneralMessageProcessor setProtocol(Protocol<VitalPB.Frame> protocol) {
        this.protocol = protocol;
        return this;
    }


    public GeneralMessageProcessor setTransmitLogic(TransmitLogic transmitLogic) {
        this.transmitLogic = transmitLogic;
        return this;
    }


    public GeneralMessageProcessor setExecutor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }
}
