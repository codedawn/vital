package com.codedawn.vital.server;

import com.codedawn.vital.server.connector.TCPConnector;
import com.codedawn.vital.server.proto.ProtocolManager;
import com.codedawn.vital.server.proto.VitalProtocol;

/**
 * @author codedawn
 * @date 2021-07-21 14:21
 */
public class Launcher {


    private static TCPConnector tcpConnector = new TCPConnector();
    public static void main(String[] args) {
        ProtocolManager.registerProtocol(VitalProtocol.class.getSimpleName(),new VitalProtocol());
        tcpConnector.start();
//        SendQos.getInstance().start();
//        SendQos.getInstance().shutdown();
//        SendQos.getInstance().start();
    }
}
