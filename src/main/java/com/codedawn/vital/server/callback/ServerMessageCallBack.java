package com.codedawn.vital.server.callback;

import com.codedawn.vital.proto.MessageWrapper;

/**
 * @author codedawn
 * @date 2021-07-27 20:47
 */
public interface ServerMessageCallBack<T extends MessageWrapper> {
    /**
     * ack到达时调用
     * @param messageWrapper
     */
    public void ackArrived(T messageWrapper);
}
