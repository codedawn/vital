package com.codedawn.vital.session;


import com.codedawn.vital.util.StringUtils;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理connection的
 * @author codedawn
 * @date 2021-07-23 11:31
 */
public class ConnectionManage {

    private static Logger log = LoggerFactory.getLogger(ConnectionManage.class);

    private ConcurrentHashMap<String, Channel> connections = new ConcurrentHashMap<>();



    public ConnectionManage() {

    }


    public void add(Connection connection) {
        Channel channel = connections.get(connection.getId());
        if (channel != null) {
            channel.close();
        }
        connections.put(connection.getId(), connection.getChannel());
        log.info("用户：{}认证成功",connection.getId());
        log.info("当前在线用户: {}",connections.size());

    }

    public Connection get(String id) {
        if (StringUtils.isEmpty(id)) {
            log.warn("id为空");
            return null;
        }
        Channel channel = connections.get(id);
        if (channel != null) {
            Connection connection = channel.attr(Connection.CONNECTION).get();
            if (connection != null) {
                return connection;
            }
        }
        return null;
    }

    public void remove(Connection connection) {
        connections.remove(connection.getId());
        log.info("用户：{}登出",connection.getId());
        log.info("当前在线用户: {}",connections.size());
    }

}
