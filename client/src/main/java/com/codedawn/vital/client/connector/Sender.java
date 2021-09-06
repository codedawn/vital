package com.codedawn.vital.client.connector;

import com.codedawn.vital.server.callback.RequestSendCallBack;
import com.codedawn.vital.server.callback.SendCallBack;
import com.codedawn.vital.server.proto.MessageWrapper;
import io.netty.channel.Channel;

/**
 * @author codedawn
 * @date 2021-07-26 23:46
 */
public interface Sender<T,E extends MessageWrapper>{
    void send(T frame);

    void send(E messageWrapper);

    /** 适用于qos，responseCallBack回调
     * @param frame
     * @param sendCallBack 消息回调接口
     */
    void send(T frame, SendCallBack sendCallBack);


    void send(Channel channel, T frame, RequestSendCallBack requestSendCallBack);
}
