package com.codedawn.vital.server.callback;

import com.codedawn.vital.server.proto.MessageWrapper;

/**
 * @author codedawn
 * @date 2021-08-14 19:12
 */
public interface RequestSendCallBack<T extends MessageWrapper> extends SendCallBack<T>{

    /**
     * 响应到达时触发
     * @param response
     */
    public void onResponse(T response);

}
