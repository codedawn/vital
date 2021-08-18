package com.codedawn.vital.callback;

import com.codedawn.vital.proto.MessageWrapper;

/**
 * @author codedawn
 * @date 2021-07-26 23:53
 */
public interface ResponseCallBack <T extends MessageWrapper>{

    /**
     * ack到达时触发该方法
     * @param messageWrapper
     */
    public void ackArrived(T messageWrapper);

    /**
     * 发送异常时触发该方法
     * @param messageWrapper
     */
    public void exception(T messageWrapper);
}
