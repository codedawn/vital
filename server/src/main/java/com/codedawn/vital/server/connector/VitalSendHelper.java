package com.codedawn.vital.server.connector;


import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;
import com.codedawn.vital.server.qos.SendQos;
import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionManage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-29 10:18
 */
public class VitalSendHelper {


    private ConnectionManage connectionManage;

    private SendQos sendQos;

    public VitalSendHelper() {

    }


    public VitalSendHelper setConnectionManage(ConnectionManage connectionManage) {
        this.connectionManage = connectionManage;
        return this;
    }

    public VitalSendHelper setSendQos(SendQos sendQos) {
        this.sendQos = sendQos;
        return this;
    }

    private static Logger log = LoggerFactory.getLogger(VitalSendHelper.class);

    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param message
     */
    public  void send(Channel channel, VitalPB.Protocol message) {
        if(message.getHeader().getIsQos()){
            VitalMessageWrapper vitalMessageWrapper = new VitalMessageWrapper(message);
            send(channel,vitalMessageWrapper);
        }else {
            send0(channel,message);
        }

    }

    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param vitalMessageWrapper
     */
    public  void send(Channel channel, VitalMessageWrapper vitalMessageWrapper) {

        if (vitalMessageWrapper.getIsQos()) {
            sendQos.addIfAbsent(vitalMessageWrapper.getSeq(),vitalMessageWrapper);
        }else {
//            log.info("设置了MessageCallBack，但是没有开启qos，所以永远不会调用MessageCallBack");
        }
        send0(channel,vitalMessageWrapper.getProtocol());
    }

    /**
     * 直接调用该方法，默认不启用sendQos，ack消息发送直接使用该方法，ack消息不需要重发
     * @param channel
     * @param message
     */
    public  void send0(Channel channel, VitalPB.Protocol message) {
        if (channel == null) {
            log.info("VitalSendHelper#send发送消息时，channel为null");
            return;
        }

        log.debug("发送了{},seq:{}",message.toString(),message.getHeader().getSeq());
        ChannelFuture future = channel.writeAndFlush(message);
    }

    /**
     * 该方法适用于开启qos的消息
     * @param id
     * @param messageWrapper
     */
    public void send(String id, VitalMessageWrapper messageWrapper){
        Connection connection = connectionManage.get(id);
        if(connection!=null){
            send(connection.getChannel(),messageWrapper);
        }else {
            log.warn("send中id:{}对应的connection为null，说明该id不在线，需要发送的消息将无法发送",id);
        }
    }

}
