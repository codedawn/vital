package com.codedawn.vital.server.callback;

import com.codedawn.vital.server.proto.MessageWrapper;

/**
 * @author codedawn
 * @date 2021-07-26 23:53
 */
public interface SendCallBack<T extends MessageWrapper>{

    /**
     * ack到达时触发该方法
     * @param messageWrapper 源消息
     */
    public void onAck(T messageWrapper);

    /**
     * 发送异常时触发该方法
     * @param exception
     */
    public void onException(T exception);
}
