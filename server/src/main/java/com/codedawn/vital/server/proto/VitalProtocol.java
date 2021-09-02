package com.codedawn.vital.server.proto;

import com.codedawn.vital.server.command.CommandHandler;
import com.codedawn.vital.server.connector.VitalSendHelper;
import com.codedawn.vital.server.factory.VitalMessageFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * @author codedawn
 * @date 2021-07-25 10:10
 */
public class VitalProtocol implements Protocol<VitalPB.Protocol> {

    private VitalSendHelper vitalSendHelper;

    private ProtobufEncoder encode;

    private ProtobufDecoder decode;

    private CommandHandler commandHandler;


    public VitalProtocol(CommandHandler commandHandler) {
        this.encode = new ProtobufEncoder();
        this.decode = new ProtobufDecoder(VitalProtobuf.Protocol.getDefaultInstance());
        this.commandHandler = commandHandler;

    }


    public VitalProtocol setVitalSendHelper(VitalSendHelper vitalSendHelper) {
        this.vitalSendHelper = vitalSendHelper;
        return this;
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

    @Override
    public VitalPB.Protocol createAuthRequest(String id, String token){
        return VitalMessageFactory.createAuthRequest(id,token);
    }

    @Override
    public VitalPB.Protocol createAck(VitalPB.Protocol message) {
        return VitalMessageFactory.createAck(message);
    }

    @Override
    public VitalPB.Protocol createAckWithExtra(VitalPB.Protocol message, String perId, long timeStamp) {
        return VitalMessageFactory.createAckWithExtra(message, perId,timeStamp);
    }

    @Override
    public VitalPB.Protocol createDisAuthSuccess(String id) {
        return VitalMessageFactory.createDisAuthSuccess(id);
    }

    @Override
    public VitalPB.Protocol createException(String seq, String extra, int code) {
        return VitalMessageFactory.createException(seq,extra,code);
    }

    @Override
    public VitalPB.Protocol createAuthSuccess(String id) {
        return VitalMessageFactory.createAuthSuccess(id);
    }


    @Override
    public  VitalPB.Protocol createTextMessage(String fromId,String toId, String message){
        return VitalMessageFactory.createTextMessage(fromId,toId,message);
    }

    @Override
    public  VitalPB.Protocol createHeartBeat(){
        return VitalMessageFactory.createHeartBeat();
    }
    /**
     * 直接调用该方法，默认不启用sendQos，ack消息发送直接使用该方法，ack消息不需要重发,或者由sendQos发送的消息
     *
     * @param channel
     * @param message
     */
    @Override
    public void send0(Channel channel, VitalPB.Protocol message) {
        vitalSendHelper.send0(channel,message);
    }


    /**
     * 该方法适用于开启qos的消息
     * @param id
     * @param messageWrapper
     */
    @Override
    public void send(String id, MessageWrapper messageWrapper) {
        vitalSendHelper.send(id, (VitalMessageWrapper) messageWrapper);
    }

    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param message
     */
    @Override
    public void send(Channel channel, VitalPB.Protocol message) {
        vitalSendHelper.send(channel,message);
    }

    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param messageWrapper
     */
    @Override
    public void send(Channel channel, MessageWrapper messageWrapper) {
        vitalSendHelper.send(channel, (VitalMessageWrapper) messageWrapper);
    }
}
