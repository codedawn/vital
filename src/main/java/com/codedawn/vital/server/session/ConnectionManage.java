package com.codedawn.vital.server.session;


import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author codedawn
 * @date 2021-07-23 11:31
 */
public class ConnectionManage {

    private static Logger log = LoggerFactory.getLogger(ConnectionManage.class);

    private ConcurrentHashMap<String, Channel> connections = new ConcurrentHashMap<>();



    public ConnectionManage() {

    }


    public void add(Connection connection) {
        connections.put(connection.getId(), connection.getChannel());
        log.info("user {} login success",connection.getId());

    }

    public Connection get(String id) {
        return connections.get(id).attr(Connection.CONNECTION).get();
    }

    public void remove(Connection connection) {
        connections.remove(connection.getId());
        log.info("user {} logout",connection.getId());
    }

}
