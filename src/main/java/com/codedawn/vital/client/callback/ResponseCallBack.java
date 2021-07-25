package com.codedawn.vital.client.callback;

import com.codedawn.vital.proto.MessageWrapper;

/**
 * @author codedawn
 * @date 2021-07-26 23:53
 */
public interface ResponseCallBack <T extends MessageWrapper>{

    /**
     * ack到达时调用
     * @param messageWrapper
     */
    public void ackArrived(T messageWrapper);
}
