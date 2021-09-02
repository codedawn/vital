package com.codedawn.vital.client.connector;

import com.codedawn.vital.client.qos.ClientSendQos;
import com.codedawn.vital.server.callback.ResponseCallBack;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;
import com.codedawn.vital.server.proto.VitalProtobuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author codedawn
 * @date 2021-07-25 8:53
 */
public class VitalSender implements Sender<VitalProtobuf.Protocol,VitalMessageWrapper> {


    private static Logger log = LoggerFactory.getLogger(VitalSender.class);


    private Channel channel;

    private ClientSendQos clientSendQos;

    private Protocol<VitalPB.Protocol> protocol;
    /**
     * 消息回调，ack到达时调用
     */
    private ConcurrentHashMap<String, ResponseCallBack> messageCallBackMap = new ConcurrentHashMap<>();



    public VitalSender(ClientSendQos clientSendQos) {
        this();
        this.clientSendQos = clientSendQos;
    }

    private VitalSender() {

    }


    /**
     * 如果需要回调应该使用{@link VitalSender#send(VitalPB.Protocol, ResponseCallBack)}
     * @param message
     */
    @Override
    public void send(VitalPB.Protocol message) {
        protocol.send(channel,message);
    }

    /**
     * @param message
     * @param responseCallBack 消息回调接口
     */
    @Override
    public void send(VitalPB.Protocol message, ResponseCallBack responseCallBack) {

        if (message.getHeader().getIsQos()) {
            messageCallBackMap.putIfAbsent(message.getHeader().getSeq(), responseCallBack);
        }else {
            log.info("设置了MessageCallBack，但是没有开启qos，所以永远不会调用MessageCallBack");
        }
        send(message);
    }

    /**
     * ack到达，调用消息回调
     * @param vitalMessageWrapper
     */
    @Override
    public void invokeCallback(VitalMessageWrapper vitalMessageWrapper) {
        boolean isAckExtra = vitalMessageWrapper.getIsAckExtra();
        String ackSeq;
        if(isAckExtra){
            VitalPB.AckMessageWithExtra ackMessageWithExtra = vitalMessageWrapper.getMessage();
            ackSeq = ackMessageWithExtra.getAckSeq();
        }else {
            VitalPB.AckMessage ackMessage = vitalMessageWrapper.getMessage();
            ackSeq = ackMessage.getAckSeq();
        }
        ResponseCallBack responseCallBack = messageCallBackMap.get(ackSeq);
        if (responseCallBack != null) {
            responseCallBack.onAck(vitalMessageWrapper);

        }
    }

    /**
     * 异常到达，调用消息回调
     * @param vitalMessageWrapper
     */
    @Override
    public void invokeExceptionCallback(VitalMessageWrapper vitalMessageWrapper) {
        VitalPB.ExceptionMessage exceptionMessage = vitalMessageWrapper.getMessage();
        ResponseCallBack responseCallBack = messageCallBackMap.get(exceptionMessage.getExceptionSeq());
        if (responseCallBack != null) {
            responseCallBack.exception(vitalMessageWrapper);

        }
    }



    public VitalSender setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }
}
