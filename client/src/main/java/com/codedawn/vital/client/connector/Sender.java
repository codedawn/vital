package com.codedawn.vital.client.connector;

import com.codedawn.vital.server.callback.ResponseCallBack;
import com.codedawn.vital.server.proto.MessageWrapper;

/**
 * @author codedawn
 * @date 2021-07-26 23:46
 */
public interface Sender<T,E extends MessageWrapper>{
    void send(T message);

    void send(E messageWrapper);

    /** 适用于qos，responseCallBack回调
     * @param message
     * @param responseCallBack 消息回调接口
     */
    void send(T message, ResponseCallBack responseCallBack);

    void invokeCallback(E messageWrapper);

    void invokeExceptionCallback(E messageWrapper);

}
