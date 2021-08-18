package com.codedawn.vital.proto;

import com.codedawn.vital.command.CommandHandler;
import io.netty.channel.ChannelHandler;

/**
 * 协议类
 * @author codedawn
 * @date 2021-07-25 9:54
 */
public interface Protocol {
    CommandHandler getCommandHandler();

    ChannelHandler getEncode();

    ChannelHandler getDecode();


    ChannelHandler getFrameDecode();

    ChannelHandler getLengthFieldPrepender();
}
