package com.codedawn.vital.client.session;

import com.codedawn.vital.server.session.ConnectionEventListener;

/**
 * 事件监听，然后派发到对应eventProcessor
 * @author codedawn
 * @date 2021-07-28 10:52
 */
public class ClientConnectionEventListener extends ConnectionEventListener {

    public ClientConnectionEventListener() {
        super.executor=null;
    }


}
