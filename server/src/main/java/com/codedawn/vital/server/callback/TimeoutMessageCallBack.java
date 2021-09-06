package com.codedawn.vital.server.callback;

import com.codedawn.vital.server.proto.MessageWrapper;

import java.util.List;

/**
 * @author codedawn
 * @date 2021-08-05 19:17
 */
public interface TimeoutMessageCallBack<T extends MessageWrapper> {

    public void onTimeout(List<T> timeoutMessages);
}
