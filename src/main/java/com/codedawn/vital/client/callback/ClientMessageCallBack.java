package com.codedawn.vital.client.callback;

import com.codedawn.vital.proto.MessageWrapper;

/**
 * @author codedawn
 * @date 2021-07-27 14:14
 */
public interface ClientMessageCallBack<T extends MessageWrapper> {
    /**
     * 消息到达时调用
     * @param messageWrapper
     */
    public void messageArrive(T messageWrapper);
}
