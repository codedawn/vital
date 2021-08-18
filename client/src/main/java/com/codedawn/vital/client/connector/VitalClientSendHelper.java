package com.codedawn.vital.client.connector;


import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalProtobuf;
import com.codedawn.vital.client.qos.ClientSendQos;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-29 10:18
 */
public class VitalClientSendHelper {

    private VitalClientSendHelper() {

    }


    private static Logger log = LoggerFactory.getLogger(VitalClientSendHelper.class);

    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param message
     * @param clientSendQos
     */
    public static void send(Channel channel, VitalProtobuf.Protocol message, ClientSendQos clientSendQos) {

        VitalMessageWrapper vitalMessageWrapper = new VitalMessageWrapper(message);
        if (vitalMessageWrapper.getQos()) {
            vitalMessageWrapper.setChannel(channel);
            clientSendQos.addIfAbsent(vitalMessageWrapper.getQosId(),vitalMessageWrapper);
        }else {
            log.info("设置了MessageCallBack，但是没有开启qos，所以永远不会调用MessageCallBack");
        }
        send(channel,message);
    }

    /**
     *  查看消息是否可以通行，auth和heartbeat,ack可以未认证发送
     */
    private static boolean checkPermit(VitalProtobuf.Protocol msg) {
        VitalProtobuf.DataType dataType =  msg.getDataType();
        if (dataType == VitalProtobuf.DataType.AuthMessageType
                || dataType == VitalProtobuf.DataType.HeartbeatType
                ||dataType== VitalProtobuf.DataType.AckMessageType
                ||dataType== VitalProtobuf.DataType.AckMessageWithExtraType) {
            return true;
        }
        return false;
    }

    /**
     * 直接调用该方法，默认不启用qos
     * @param channel
     * @param message
     */
    public static void send(Channel channel, VitalProtobuf.Protocol message) {

        if (channel == null) {
            log.info("发送消息时，channel为null,说明未连接");
            return;
        }

        //未认证并且是需要认证才能发送的消息
        if (!TCPConnect.isAuth(channel)&&!checkPermit(message)) {
            log.info("发送的消息需要认证,当前未认证，服务器将不回应该消息");
        }


        log.debug("发送了{},qosId:{}",message.toString(),message.getQosId());
        ChannelFuture future = channel.writeAndFlush(message);
    }


}
