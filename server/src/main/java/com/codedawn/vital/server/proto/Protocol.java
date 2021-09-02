package com.codedawn.vital.server.proto;

import com.codedawn.vital.server.command.CommandHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

/**
 * 协议类
 * @author codedawn
 * @date 2021-07-25 9:54
 */
public interface Protocol<T>{
    CommandHandler getCommandHandler();

    ChannelHandler getEncode();

    ChannelHandler getDecode();


    ChannelHandler getFrameDecode();

    ChannelHandler getLengthFieldPrepender();

    T createAuthRequest(String id, String token);

    T createAck(T message);

    T createAckWithExtra(T message, String perId, long timeStamp);

    T createDisAuthSuccess(String id);

    T createException(String qosId, String extra, int code);

    T createAuthSuccess(String id);

    T createTextMessage(String fromId,String toId, String message);

    T createHeartBeat();

    /**
     * 直接调用该方法，默认不启用sendQos，ack消息发送直接使用该方法，ack消息不需要重发
     * @param channel
     * @param message
     */
    void send0(Channel channel, T message);


    /**
     * 该方法适用于开启qos的消息
     * @param id
     * @param messageWrapper
     */
    void send(String id, MessageWrapper messageWrapper);


    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param message
     */
    void send(Channel channel, T message);

    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param messageWrapper
     */
    void send(Channel channel, MessageWrapper messageWrapper);

}
