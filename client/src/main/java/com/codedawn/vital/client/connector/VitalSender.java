package com.codedawn.vital.client.connector;

import com.codedawn.vital.server.callback.ResponseCallBack;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalProtobuf;
import com.codedawn.vital.client.qos.ClientSendQos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author codedawn
 * @date 2021-07-25 8:53
 */
public class VitalSender implements Sender {


    private static Logger log = LoggerFactory.getLogger(VitalSender.class);

    private TCPConnect tcpConnect;

    private ClientSendQos clientSendQos;

    /**
     * 消息回调，ack到达时调用
     */
    private ConcurrentHashMap<String, ResponseCallBack> messageCallBackMap = new ConcurrentHashMap<>();

    private List<Object> retainMessage = new ArrayList<>();


    public VitalSender(TCPConnect tcpConnect, ClientSendQos clientSendQos) {
        this();
        this.tcpConnect = tcpConnect;
        this.clientSendQos = clientSendQos;
    }

    public VitalSender(ClientSendQos clientSendQos) {
        this();
        this.clientSendQos = clientSendQos;
    }

    private VitalSender() {

    }


    /**
     * 直接使用本方法，即时设置了qos也不起作用，应该使用{@link VitalSender#send(Object, ResponseCallBack)}
     * @param protocol
     */
    @Override
    public void send(Object protocol) {
        VitalProtobuf.Protocol message = (VitalProtobuf.Protocol) protocol;

        VitalClientSendHelper.send(tcpConnect.getChannel(),message, clientSendQos);
    }

    /**
     * @param protocol
     * @param responseCallBack 消息回调接口
     */
    @Override
    public void send(Object protocol, ResponseCallBack responseCallBack) {
        VitalProtobuf.Protocol message = (VitalProtobuf.Protocol) protocol;
        if (message.getQos()) {
            messageCallBackMap.putIfAbsent(message.getQosId(), responseCallBack);
        }else {
            log.info("设置了MessageCallBack，但是没有开启qos，所以永远不会调用MessageCallBack");
        }
        send(protocol);
    }

    /**
     * ack到达，调用消息回调
     * @param messageWrapper
     */
    @Override
    public void invokeCallback(Object messageWrapper) {
        VitalMessageWrapper message = (VitalMessageWrapper) messageWrapper;
        ResponseCallBack responseCallBack = messageCallBackMap.get(message.getAckQosId());
        if (responseCallBack != null) {
            responseCallBack.onAck(message);

        }
    }

    /**
     * 异常到达，调用消息回调
     * @param messageWrapper
     */
    @Override
    public void invokeExceptionCallback(Object messageWrapper) {
        VitalMessageWrapper message = (VitalMessageWrapper) messageWrapper;
        ResponseCallBack responseCallBack = messageCallBackMap.get(message.getExceptionQosId());
        if (responseCallBack != null) {
            responseCallBack.exception(message);

        }
    }

    @Override
    public VitalSender setTcpConnect(TCPConnect tcpConnect) {
        this.tcpConnect = tcpConnect;
        return this;
    }

    @Override
    public void sendRetainMessage() {
        Iterator<Object> iterator = retainMessage.iterator();
        while (iterator.hasNext()) {
            send(iterator.next());
            iterator.remove();
        }
    }
}
