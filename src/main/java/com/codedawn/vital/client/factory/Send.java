package com.codedawn.vital.client.factory;

import com.codedawn.vital.client.connector.TCPConnect;
import io.netty.channel.ChannelFuture;

/**
 * @author codedawn
 * @date 2021-07-25 8:53
 */
public class Send {
    private TCPConnect tcpConnect;

    public Send() {
        tcpConnect = TCPConnect.getInstance();
    }

    private static Send instance;

    public static Send getInstance() {
        if (instance == null) {
            instance = new Send();
        }
        return instance;
    }
    public void send(VitalProtocol.Protocol protocol) {
        ChannelFuture future = tcpConnect.getChannel().writeAndFlush(protocol);

    }
}
