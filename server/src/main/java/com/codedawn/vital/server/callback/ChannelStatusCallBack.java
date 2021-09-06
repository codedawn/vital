package com.codedawn.vital.server.callback;

import io.netty.channel.Channel;

/**
 * @author codedawn
 * @date 2021-08-14 14:05
 */
public interface ChannelStatusCallBack {
    void onOpen(Channel channel);

    void onClose(Channel channel);
}
