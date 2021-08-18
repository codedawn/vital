package com.codedawn.vital.client.session.impl;

import com.codedawn.vital.client.connector.TCPConnect;
import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端的断开连接事件处理器，这是指建立connection之后的断开，也就是认证通过之后
 * @author codedawn
 * @date 2021-07-31 10:30
 */
public class ClientDisconnectEventProcessor implements ConnectionEventProcessor {

    private static Logger log = LoggerFactory.getLogger(ClientDisconnectEventProcessor.class);

    private TCPConnect tcpConnect;

    public ClientDisconnectEventProcessor(TCPConnect tcpConnect) {
        this.tcpConnect = tcpConnect;
    }

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        tcpConnect.shutdown();
        if (tcpConnect.isConnect()) {
            tcpConnect.start();
        }
    }
}
