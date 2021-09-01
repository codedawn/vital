package com.codedawn.vital.client.connector;

import com.codedawn.vital.client.qos.ClientSendQos;
import com.codedawn.vital.server.callback.ResponseCallBack;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
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
     * 直接使用本方法，即时设置了qos也不起作用，应该使用{@link VitalSender#send(VitalProtobuf.Protocol, ResponseCallBack)}
     * @param message
     */
    @Override
    public void send(VitalProtobuf.Protocol message) {
        VitalClientSendHelper.send(channel,message, clientSendQos);
    }

    /**
     * @param message
     * @param responseCallBack 消息回调接口
     */
    @Override
    public void send(VitalProtobuf.Protocol message, ResponseCallBack responseCallBack) {

        if (message.getQos()) {
            messageCallBackMap.putIfAbsent(message.getQosId(), responseCallBack);
        }else {
            log.info("设置了MessageCallBack，但是没有开启qos，所以永远不会调用MessageCallBack");
        }
        send(message);
    }

    /**
     * ack到达，调用消息回调
     * @param message
     */
    @Override
    public void invokeCallback(VitalMessageWrapper message) {
        ResponseCallBack responseCallBack = messageCallBackMap.get(message.getAckQosId());
        if (responseCallBack != null) {
            responseCallBack.onAck(message);

        }
    }

    /**
     * 异常到达，调用消息回调
     * @param message
     */
    @Override
    public void invokeExceptionCallback(VitalMessageWrapper message) {
        ResponseCallBack responseCallBack = messageCallBackMap.get(message.getExceptionQosId());
        if (responseCallBack != null) {
            responseCallBack.exception(message);

        }
    }



    public VitalSender setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }
}
