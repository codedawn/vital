package com.codedawn.vital.server.proto;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author codedawn
 * @date 2021-07-25 9:51
 */
public class ProtocolManager {
    private static final ConcurrentHashMap<String, Protocol> protocols = new ConcurrentHashMap<>();

    public static Protocol getProtocol(String name) {
        return protocols.get(name);
    }

    public static void registerProtocol(String name,Protocol protocol) {
        protocols.put(name, protocol);
    }
}
