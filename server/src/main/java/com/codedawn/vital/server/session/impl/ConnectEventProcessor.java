package com.codedawn.vital.server.session.impl;

import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionEventProcessor;
import com.codedawn.vital.server.session.ConnectionManage;

/**
 * @author codedawn
 * @date 2021-08-18 20:28
 */
public class ConnectEventProcessor implements ConnectionEventProcessor {

    private ConnectionManage connectionManage;

    public ConnectEventProcessor(ConnectionManage connectionManage) {
        this.connectionManage = connectionManage;
    }

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        if (connection != null) {
            connectionManage.add(connection);
        }
    }
}
