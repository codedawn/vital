package com.codedawn.vital.client.connector;

import com.codedawn.vital.server.callback.ResponseCallBack;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;
import io.netty.channel.Channel;

/**
 * @author codedawn
 * @date 2021-07-26 23:46
 */
public interface Sender<T,E extends MessageWrapper>{
    void send(VitalPB.Protocol message);

    void send(VitalPB.Protocol message, ResponseCallBack responseCallBack);

    void invokeCallback(E messageWrapper);

    void invokeExceptionCallback(E messageWrapper);


    Sender setChannel(Channel channel);
}
