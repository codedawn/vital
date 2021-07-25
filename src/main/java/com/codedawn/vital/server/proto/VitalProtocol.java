package com.codedawn.vital.server.proto;

import com.codedawn.vital.server.command.CommandHandler;
import com.codedawn.vital.server.command.DefaultCommandHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * @author codedawn
 * @date 2021-07-25 10:10
 */
public class VitalProtocol implements Protocol{
    private ProtobufEncoder encode;

    private ProtobufDecoder decode;

    private CommandHandler commandHandler;

    public VitalProtocol() {
        this.encode = new ProtobufEncoder();
        this.decode = new ProtobufDecoder(VitalProtobuf.Protocol.getDefaultInstance());
        this.commandHandler = new DefaultCommandHandler();
    }

    @Override
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    @Override
    public ProtobufEncoder getEncode() {
        return encode;
    }

    @Override
    public ProtobufDecoder getDecode() {
        return decode;
    }
}
