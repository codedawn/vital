package com.codedawn.vital.server.callback;

import com.codedawn.vital.server.proto.MessageWrapper;

/**
 * @author codedawn
 * @date 2021-08-14 19:12
 */
public interface AuthResponseCallBack<T extends MessageWrapper> extends ResponseCallBack<T>{

    public void success(T messageWrapper);
}
