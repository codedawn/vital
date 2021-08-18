package com.codedawn.vital.session.impl;

import com.codedawn.vital.session.Connection;
import com.codedawn.vital.session.ConnectionEventProcessor;

/**
 * 认证成功后会绑定connection到channel，该时机触发connect事件
 * @author codedawn
 * @date 2021-07-31 11:11
 */
public class ClientConnectEventProcessor implements ConnectionEventProcessor {
    @Override
    public void onEvent(String remoteAddress, Connection connection) {

    }
}
