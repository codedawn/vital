package com.codedawn.vital.client.connector;

import com.codedawn.vital.client.callback.ResponseCallBack;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.qos.SendQos;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author codedawn
 * @date 2021-07-25 8:53
 */
public class VitalSender implements Sender {


    private static Logger log = LoggerFactory.getLogger(VitalSender.class);

    private TCPConnect tcpConnect;

    private SendQos sendQos;

    private ConcurrentHashMap<String, ResponseCallBack> messageCallBackMap = new ConcurrentHashMap<>();

    public VitalSender(TCPConnect tcpConnect, SendQos sendQos) {
        this.tcpConnect = tcpConnect;
        this.sendQos = sendQos;
    }

    @Override
    public void send(Object protocol) {
        VitalProtobuf.Protocol message = (VitalProtobuf.Protocol) protocol;

        log.info("客户端发送消息：{}",message.toString());
        ChannelFuture future = tcpConnect.getChannel().writeAndFlush(message);
    }

    @Override
    public void send(Object protocol, ResponseCallBack responseCallBack) {
        VitalProtobuf.Protocol message = (VitalProtobuf.Protocol) protocol;

        VitalMessageWrapper vitalMessageWrapper = new VitalMessageWrapper(message);
        if (vitalMessageWrapper.getQos()) {
            sendQos.add(vitalMessageWrapper.getQosId(),vitalMessageWrapper);
            messageCallBackMap.putIfAbsent(vitalMessageWrapper.getQosId(), responseCallBack);
        }else {
            log.info("设置了MessageCallBack，但是没有开启qos，所以永远不会调用MessageCallBack");
        }
        send(protocol);
    }

    @Override
    public void invokeCallback(Object messageWrapper) {
        VitalMessageWrapper message = (VitalMessageWrapper) messageWrapper;
        ResponseCallBack responseCallBack = messageCallBackMap.get(message.getAckQosId());
        if (responseCallBack != null) {
            responseCallBack.ackArrived(message);
        }
    }
}
