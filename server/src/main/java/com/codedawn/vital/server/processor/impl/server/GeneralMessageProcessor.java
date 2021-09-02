package com.codedawn.vital.server.processor.impl.server;

import com.codedawn.vital.server.context.DefaultMessageContext;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
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
public class GeneralMessageProcessor implements Processor<DefaultMessageContext, VitalMessageWrapper> {
    private static Logger log = LoggerFactory.getLogger(GeneralMessageProcessor.class);

    private ExecutorService executor;


    private Protocol<VitalPB.Protocol> protocol;

    public GeneralMessageProcessor(ExecutorService executor, ConnectionManage connectionManage, SendQos sendQos) {
        this.executor = executor;
    }

    public GeneralMessageProcessor() {

    }

    private Transmitter transmitter=new Transmitter() {
        @Override
        public List<String> onGroup(DefaultMessageContext defaultMessageContext, String toId) {
            return null;
        }

        @Override
        public String onOne(DefaultMessageContext defaultMessageContext, String toId) {
            return toId;
        }
    };


    @Override
    public void process(DefaultMessageContext defaultMessageContext, VitalMessageWrapper vitalMessageWrapper) {


        List<String> idList;
        if (vitalMessageWrapper.getIsGroup()){
            //群发
            idList = transmitter.onGroup(defaultMessageContext, vitalMessageWrapper.getToId());
            if (idList == null||idList.size()==0) {
                log.info("发送群组消息时，发送id列表为空,seq:{}群组消息将不转发",vitalMessageWrapper.getSeq());
                return;
            }
            for (String id : idList) {
                //转发
                protocol.send(id,vitalMessageWrapper);
            }

        }else {
            String id = transmitter.onOne(defaultMessageContext, vitalMessageWrapper.getToId());
            if(StringUtils.isEmpty(id)){
                log.info("发送单聊消息时，发送id为空,seq:{}单聊消息将不转发",vitalMessageWrapper.getSeq());
                return;
            }
            //转发
            protocol.send(id,vitalMessageWrapper);
        }


    }




    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    public Protocol<VitalPB.Protocol> getProtocol() {
        return protocol;
    }

    public GeneralMessageProcessor setProtocol(Protocol<VitalPB.Protocol> protocol) {
        this.protocol = protocol;
        return this;
    }

    public GeneralMessageProcessor setTransmitter(Transmitter transmitter) {
        this.transmitter = transmitter;
        return this;
    }
}
