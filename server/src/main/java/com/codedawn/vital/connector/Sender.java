package com.codedawn.vital.connector;

import com.codedawn.vital.callback.ResponseCallBack;

/**
 * @author codedawn
 * @date 2021-07-26 23:46
 */
public interface Sender {
    void send(Object protocol);

    void send(Object protocol, ResponseCallBack responseCallBack);

    void invokeCallback(Object messageWrapper);

    void invokeExceptionCallback(Object messageWrapper);

    Sender setTcpConnect(TCPConnect tcpConnect);
}
