package com.codedawn.vital.callback;

import io.netty.channel.Channel;

/**
 * @author codedawn
 * @date 2021-08-14 14:05
 */
public interface ChannelStatusCallBack {
    void open(Channel channel);

    void close(Channel channel);
}
