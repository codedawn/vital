package com.codedawn.vital.server.session;


import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.util.AddressUtil;
import com.codedawn.vital.server.util.StringUtils;
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

    private Protocol protocol;

    public ConnectionManage() {

    }


    public void add(Connection connection) {

        Channel channel=connections.put(connection.getId(), connection.getChannel());

        if (channel!=null) {
            //todo 不同ip登录需要踢人
            if(!AddressUtil.parseRemoteIP(channel).equals(AddressUtil.parseRemoteIP(connection.getChannel()))){
                protocol.send(channel,protocol.createKickoutMessage());
            }
            channel.close();
        }
        log.info("用户：{}登入",connection.getId());
        log.info("当前在线用户: {}",connections.size());

    }

    /**
     *
     * @param id
     * @return 返回对应id的connection，不存在返回null
     */
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
        if(connections.remove(connection.getId(), connection.getChannel())){
            log.info("用户：{}登出",connection.getId());
            log.info("当前在线用户: {}",connections.size());
        }
    }

}
