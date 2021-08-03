package com.codedawn.vital.session.impl;

import com.codedawn.vital.session.Connection;
import com.codedawn.vital.session.ConnectionEventProcessor;

/**
 * @author codedawn
 * @date 2021-07-31 11:11
 */
public class ClientConnectEventProcessor implements ConnectionEventProcessor {
    @Override
    public void onEvent(String remoteAddress, Connection connection) {
//        VitalProtobuf.Protocol disAuth = VitalMessageFactory.createDisAuth(connection.getId());
//        TCPClient.sender.send(disAuth);
    }
}
