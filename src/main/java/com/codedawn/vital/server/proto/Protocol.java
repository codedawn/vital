package com.codedawn.vital.server.proto;

import com.codedawn.vital.server.command.CommandHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * @author codedawn
 * @date 2021-07-25 9:54
 */
public interface Protocol {
    CommandHandler getCommandHandler();

    ProtobufEncoder getEncode();

    ProtobufDecoder getDecode();
}
