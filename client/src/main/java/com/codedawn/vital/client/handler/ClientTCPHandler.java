package com.codedawn.vital.client.handler;
import com.codedawn.vital.server.handler.TCPBusHandler;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.ProtocolManager;

public class ClientTCPHandler extends TCPBusHandler{

    public ClientTCPHandler(Class<? extends Protocol> protocolClass, ProtocolManager protocolManager) {
        super(protocolClass, protocolManager);
    }
}