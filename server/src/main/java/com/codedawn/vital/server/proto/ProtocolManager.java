package com.codedawn.vital.server.proto;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 协议管理器
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
