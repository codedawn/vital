package com.codedawn.vital.session.impl;

import com.codedawn.vital.session.Connection;
import com.codedawn.vital.session.ConnectionEventProcessor;
import com.codedawn.vital.session.ConnectionManage;

/**
 * 服务端断开事件的监听Processor
 * @author codedawn
 * @date 2021-07-28 11:34
 */
public class DisconnectEventProcessor implements ConnectionEventProcessor {

    private ConnectionManage connectionManage;

    public DisconnectEventProcessor(ConnectionManage connectionManage) {
        this.connectionManage = connectionManage;
    }

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        if (connection != null) {
            connectionManage.remove(connection);
        }
    }
}
