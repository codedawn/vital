package com.codedawn.vital.server.session;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证通过后会绑定一个connection到channel
 * @author codedawn
 * @date 2021-07-23 14:29
 */
public class Connection {

    private static Logger log = LoggerFactory.getLogger(Connection.class);


    private Channel channel;

    public static final AttributeKey<Connection> CONNECTION = AttributeKey.valueOf("connection");

    private String id;

    private ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();


    public Connection(Channel channel, String id) {
        this.channel = channel;
        this.id = id;
        //让channel和connection关联
        this.channel.attr(Connection.CONNECTION).set(this);
    }


    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Connection setAttribute(String key,Object value) {
        this.attributes.put(key,value);
        return this;
    }

    public String getId() {
        return id;
    }

    public Connection setId(String id) {
        this.id = id;
        return this;
    }

    public Channel getChannel() {
        return channel;
    }

    public Connection setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }
}
