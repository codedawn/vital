package com.codedawn.vital.server.proto;

import com.codedawn.vital.server.callback.RequestSendCallBack;
import com.codedawn.vital.server.callback.SendCallBack;
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

    T createAck(T frame);

    T createAckWithExtra(T frame, String perId, long timeStamp);


    T createException(String qosId, String extra, int code);

    T createDisAuth();

    T createAuthSuccess(String seq);

    T createTextMessage(String fromId,String toId, String message);

    T createKickoutMessage();

    T createHeartBeat();

    /**
     * 直接调用该方法，默认不启用sendQos，ack消息发送直接使用该方法，ack消息不需要重发
     * @param channel
     * @param frame
     */
    void send0(Channel channel, T frame);


    /**
     * 该方法适用于开启qos的消息
     * @param id
     * @param messageWrapper
     */
    void send(String id, MessageWrapper messageWrapper);


    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param frame
     */
    void send(Channel channel, T frame);

    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param messageWrapper
     */
    void send(Channel channel, MessageWrapper messageWrapper);


    /**
     * 带回调
     * @param channel
     * @param frame
     * @param sendCallBack
     */
    void send(Channel channel, T frame, SendCallBack sendCallBack);


    /**
     * 该方法适用于开启qos的消息，带操作回调
     * @param channel
     * @param frame
     * @param requestSendCallBack
     */
    void send(Channel channel, T frame, RequestSendCallBack requestSendCallBack);

}
