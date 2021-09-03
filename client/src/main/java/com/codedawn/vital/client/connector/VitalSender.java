package com.codedawn.vital.client.connector;

import com.codedawn.vital.server.callback.ResponseCallBack;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author codedawn
 * @date 2021-07-25 8:53
 */
public class VitalSender implements Sender<VitalPB.Frame,VitalMessageWrapper> {


    private static Logger log = LoggerFactory.getLogger(VitalSender.class);


    private TCPConnect tcpConnect;

    private Protocol<VitalPB.Frame> protocol;
    /**
     * 消息回调，ack到达时调用
     */
    private ConcurrentHashMap<String, ResponseCallBack> messageCallBackMap = new ConcurrentHashMap<>();



    public VitalSender() {

    }


    /**
     * 如果需要回调应该使用{@link VitalSender#send(VitalPB.Frame, ResponseCallBack)}
     * @param message
     */
    @Override
    public void send(VitalPB.Frame message) {
        protocol.send(tcpConnect.getChannel(),message);
    }

    @Override
    public void send(VitalMessageWrapper messageWrapper) {
        protocol.send(tcpConnect.getChannel(),messageWrapper);
    }

    /** 适用于qos，responseCallBack回调
     * @param message
     * @param responseCallBack 消息回调接口
     */
    @Override
    public void send(VitalPB.Frame message, ResponseCallBack responseCallBack) {

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
        Object message = vitalMessageWrapper.getMessage();
        String ackSeq;
        if(message instanceof VitalPB.AckMessageWithExtra){
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


    public VitalSender setTcpConnect(TCPConnect tcpConnect) {
        this.tcpConnect = tcpConnect;
        return this;
    }

    public VitalSender setProtocol(Protocol<VitalPB.Frame> protocol) {
        this.protocol = protocol;
        return this;
    }
}
