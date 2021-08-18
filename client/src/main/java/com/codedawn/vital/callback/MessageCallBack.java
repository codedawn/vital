package com.codedawn.vital.callback;

import com.codedawn.vital.proto.MessageWrapper;

/**
 * @author codedawn
 * @date 2021-07-27 14:14
 */
public interface MessageCallBack<T extends MessageWrapper> {
    /**
     * 消息到达时调用
     * @param messageWrapper
     */
    public void onMessage(T messageWrapper);
}
