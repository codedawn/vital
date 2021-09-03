package com.codedawn.vital.server.callback;

import com.codedawn.vital.server.proto.MessageWrapper;

/**
 * @author codedawn
 * @date 2021-08-14 19:12
 */
public interface AuthResponseCallBack<T extends MessageWrapper>{
    /**
     * ack到达时触发该方法
     * @param messageWrapper
     */
    public void onAck(T messageWrapper);

    public void success(T messageWrapper);

    public void exception(T messageWrapper);
}
