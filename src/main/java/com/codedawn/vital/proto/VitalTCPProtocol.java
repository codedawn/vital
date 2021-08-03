package com.codedawn.vital.proto;

import com.codedawn.vital.command.CommandHandler;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * @author codedawn
 * @date 2021-07-25 10:10
 */
public class VitalTCPProtocol implements Protocol {
    private ProtobufEncoder encode;

    private ProtobufDecoder decode;

    private CommandHandler commandHandler;


    public VitalTCPProtocol(CommandHandler commandHandler) {
        this.encode = new ProtobufEncoder();
        this.decode = new ProtobufDecoder(VitalProtobuf.Protocol.getDefaultInstance());
        this.commandHandler = commandHandler;

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

    @Override
    public ChannelHandler getFrameDecode() {

        return new ProtobufVarint32FrameDecoder();
    }

    @Override
    public ChannelHandler getLengthFieldPrepender() {
        return new ProtobufVarint32LengthFieldPrepender();
    }
}