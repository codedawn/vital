package com.codedawn.vital.client.connector;

import com.codedawn.vital.client.callback.ResponseCallBack;

/**
 * @author codedawn
 * @date 2021-07-26 23:46
 */
public interface Sender {
    void send(Object protocol);

    void send(Object protocol, ResponseCallBack responseCallBack);

    void invokeCallback(Object messageWrapper);
}
