package com.codedawn.vital.server.proto;

import com.codedawn.vital.server.callback.RequestSendCallBack;
import com.codedawn.vital.server.callback.SendCallBack;
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
public class VitalProtocol implements Protocol<VitalPB.Frame> {

    private VitalSendHelper vitalSendHelper;

    private ProtobufEncoder encode;

    private ProtobufDecoder decode;

    private CommandHandler commandHandler;


    public VitalProtocol() {
    }

    public VitalProtocol(CommandHandler commandHandler) {
        this.encode = new ProtobufEncoder();
        this.decode = new ProtobufDecoder(VitalPB.Frame.getDefaultInstance());
        this.commandHandler = commandHandler;

    }

    public VitalProtocol setEncode(ProtobufEncoder encode) {
        this.encode = encode;
        return this;
    }

    public VitalProtocol setDecode(ProtobufDecoder decode) {
        this.decode = decode;
        return this;
    }

    public VitalProtocol setCommandHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        return this;
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
    public VitalPB.Frame createAuthRequest(String id, String token){
        return  VitalMessageFactory.createAuthRequest(id,token);
    }

    @Override
    public VitalPB.Frame createAck(VitalPB.Frame frame) {
        return VitalMessageFactory.createAck(frame);
    }

    @Override
    public VitalPB.Frame createAckWithExtra(VitalPB.Frame frame, String perId, long timeStamp) {
        return VitalMessageFactory.createAckWithExtra(frame, perId,timeStamp);
    }

//    @Override
//    public VitalPB.Frame createDisAuthSuccess(String id) {
//        return VitalMessageFactory.createDisAuthSuccess(id);
//    }

    @Override
    public VitalPB.Frame createException(String seq, String extra, int code) {
        return VitalMessageFactory.createException(seq,extra,code);
    }

    @Override
    public  VitalPB.Frame createDisAuth(){
        return  VitalMessageFactory.createDisAuth();
    }
    @Override
    public VitalPB.Frame createAuthSuccess(String seq) {
        return VitalMessageFactory.createAuthSuccess(seq);
    }


    @Override
    public  VitalPB.Frame createTextMessage(String fromId,String toId, String message){
        return VitalMessageFactory.createTextMessage(fromId,toId,message,false);
    }

    @Override
    public  VitalPB.Frame createImageMessage(String fromId,String toId, String url){
        return VitalMessageFactory.createImageMessage(fromId, toId, url,false);
    }


    @Override
    public  VitalPB.Frame createGroupImageMessage(String fromId,String toId, String url){
        return VitalMessageFactory.createImageMessage(fromId, toId, url,true);
    }
    @Override
    public  VitalPB.Frame createGroupTextMessage(String fromId,String toId, String message){
        return VitalMessageFactory.createTextMessage(fromId, toId, message,true);
    }

    @Override
    public  VitalPB.Frame createKickoutMessage(){
        return VitalMessageFactory.createKickoutMessage();
    }

    @Override
    public  VitalPB.Frame createHeartBeat(){
        return VitalMessageFactory.createHeartBeat();
    }
    /**
     * 直接调用该方法，默认不启用sendQos，ack消息发送直接使用该方法，ack消息不需要重发,或者由sendQos发送的消息
     *
     * @param channel
     * @param frame
     */
    @Override
    public void send0(Channel channel, VitalPB.Frame frame) {
        vitalSendHelper.send0(channel,frame);
    }


    /**
     * 该方法适用于开启qos的消息
     * @param id
     * @param messageWrapper
     */
    @Override
    public void send(String id, MessageWrapper messageWrapper) {
        vitalSendHelper.send(id,  messageWrapper);
    }

    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param frame
     */
    @Override
    public void send(Channel channel, VitalPB.Frame frame) {
        vitalSendHelper.send(channel,frame);
    }

    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param messageWrapper
     */
    @Override
    public void send(Channel channel, MessageWrapper messageWrapper) {
        vitalSendHelper.send(channel,  messageWrapper);
    }

    /**
     * 带回调
     *
     * @param channel
     * @param frame
     * @param sendCallBack
     */
    @Override
    public void send(Channel channel, VitalPB.Frame frame, SendCallBack sendCallBack) {
        vitalSendHelper.send(channel,frame, sendCallBack);
    }

    /**
     * 该方法适用于开启qos的消息，带操作回调
     * @param channel
     * @param frame
     * @param requestSendCallBack
     */
    @Override
    public void send(Channel channel, VitalPB.Frame frame, RequestSendCallBack requestSendCallBack){
        vitalSendHelper.send(channel,frame,requestSendCallBack);
    }
}
