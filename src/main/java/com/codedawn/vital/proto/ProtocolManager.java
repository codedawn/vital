package com.codedawn.vital.proto;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author codedawn
 * @date 2021-07-25 9:51
 */
public class ProtocolManager {
    private   ConcurrentHashMap<String, Protocol> protocols = new ConcurrentHashMap<>();

    public  Protocol getProtocol(String name) {
        return protocols.get(name);
    }

    public  void registerProtocol(String name,Protocol protocol) {
        protocols.put(name, protocol);
    }
}
