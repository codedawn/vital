package com.codedawn.vital.connector;


import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.qos.SendQos;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-29 10:18
 */
public class VitalSendHelper {

    private VitalSendHelper() {

    }
//    private static VitalSendHelper instance;
//
//    public VitalSendHelper getInstance() {
//        if (instance == null) {
//            instance = new VitalSendHelper();
//        }
//        return instance;
//    }

    private static Logger log = LoggerFactory.getLogger(VitalSendHelper.class);

    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param message
     * @param sendQos
     */
    public static void send(Channel channel, VitalProtobuf.Protocol message, SendQos sendQos) {

        VitalMessageWrapper vitalMessageWrapper = new VitalMessageWrapper(message);
        if (vitalMessageWrapper.getQos()) {
            sendQos.add(vitalMessageWrapper.getQosId(),vitalMessageWrapper);
        }else {
            log.info("设置了MessageCallBack，但是没有开启qos，所以永远不会调用MessageCallBack");
        }
        send(channel,message);
    }

    /**
     * 直接调用该方法，默认不启用qos
     * @param channel
     * @param message
     */
    public static void send(Channel channel, VitalProtobuf.Protocol message) {
        if (channel == null) {
            log.info("VitalSendHelper#send发送消息时，channel为null");
            return;
        }

        log.info("发送了{},qosId:{}",message.toString(),message.getQosId());
        ChannelFuture future = channel.writeAndFlush(message);
    }
}
