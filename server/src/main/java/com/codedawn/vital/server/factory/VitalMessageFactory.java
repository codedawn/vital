package com.codedawn.vital.server.factory;

import com.codedawn.vital.server.proto.VitalPB;

import java.util.UUID;

/**
 * VitalMessageFactory工厂类
 *
 * @author codedawn
 * @date 2021-07-25 22:59
 */
public class VitalMessageFactory {

    public static VitalPB.Protocol createAck(VitalPB.Protocol message) {
        VitalPB.Protocol.Builder builder = VitalPB.Protocol.newBuilder();
        builder.setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.AckMessageType)
                        .setAckMessage(VitalPB.AckMessage.newBuilder()
                                .setAckSeq(message.getHeader().getSeq())))
                .setHeader(VitalPB.Header.newBuilder()
                        .setBridge(false)
                        .setIsQos(false)
                        .setSeq(getOneUUID()));
        return builder.build();
    }

    public static VitalPB.Protocol createAckWithExtra(VitalPB.Protocol message, String perId, long timeStamp) {
        VitalPB.Protocol.Builder builder = VitalPB.Protocol.newBuilder();
        builder.setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.AckMessageWithExtraType)
                        .setAckMessageWithExtra(VitalPB.AckMessageWithExtra.newBuilder()
                                .setAckSeq(message.getHeader().getSeq())
                                .setAckPerId(perId)
                                .setAckTimeStamp(timeStamp)))
                .setHeader(VitalPB.Header.newBuilder()
                        .setBridge(false)
                        .setIsQos(false)
                        .setSeq(getOneUUID()));
        return builder.build();
    }

    public static VitalPB.Protocol createAuthRequest(String id, String token) {
        VitalPB.Protocol.Builder builder = VitalPB.Protocol.newBuilder();
        builder.setHeader(VitalPB.Header.newBuilder()
                        .setSeq(getOneUUID())
                        .setBridge(false)
                        .setIsQos(true)
                        .setIsAckExtra(false))
                .setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.AuthRequestMessageType)
                        .setAuthRequestMessage(VitalPB.AuthRequestMessage.newBuilder()
                                                .setId(id)
                                                .setToken(token)));
        return builder.build();
    }

    public static VitalPB.Protocol createAuthSuccess(String id) {
        VitalPB.Protocol.Builder builder = VitalPB.Protocol.newBuilder();
        builder.setHeader(VitalPB.Header.newBuilder()
                        .setSeq(getOneUUID())
                        .setBridge(false)
                        .setIsQos(true)
                        .setIsAckExtra(false))
                .setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.AuthSuccessMessageType)
                        .setAuthSuccessMessage(VitalPB.AuthSuccessMessage.newBuilder()
                                .setId(id)));
        return builder.build();
    }

    public static VitalPB.Protocol createException(String seq, String extra, int code) {
        VitalPB.Protocol.Builder builder = VitalPB.Protocol.newBuilder();
        builder.setHeader(VitalPB.Header.newBuilder()
                        .setSeq(getOneUUID())
                        .setBridge(false)
                        .setIsQos(true)
                        .setIsAckExtra(false))
                .setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.ExceptionMessageType)
                        .setExceptionMessage(VitalPB.ExceptionMessage.newBuilder()
                                .setCode(code)
                                .setExceptionSeq(seq)
                                .setExtra(extra)));

        return builder.build();
    }

    //todo id需不需要？
    public static VitalPB.Protocol createDisAuth(String id) {
        VitalPB.Protocol.Builder builder = VitalPB.Protocol.newBuilder();
        builder.setHeader(VitalPB.Header.newBuilder()
                        .setSeq(getOneUUID())
                        .setBridge(false)
                        .setIsQos(true)
                        .setIsAckExtra(false))
                .setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.DisAuthMessageType)
                        .setDisAuthMessage(VitalPB.DisAuthMessage.newBuilder()
                                .setId(id)));

        return builder.build();
    }

    public static VitalPB.Protocol createDisAuthSuccess(String id) {
        VitalPB.Protocol.Builder builder = VitalPB.Protocol.newBuilder();
        builder.setHeader(VitalPB.Header.newBuilder()
                        .setSeq(getOneUUID())
                        .setBridge(false)
                        .setIsQos(true)
                        .setIsAckExtra(false))
                .setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.DisAuthSuccessMessageType)
                        .setDisAuthSuccessMessage(VitalPB.DisAuthSuccessMessage.newBuilder()
                                .setId(id)));

        return builder.build();
    }




    public static VitalPB.Protocol createTextMessage(String fromId,String toId, String message) {
        VitalPB.Protocol.Builder builder = VitalPB.Protocol.newBuilder();
        builder.setHeader(VitalPB.Header.newBuilder()
                        .setSeq(getOneUUID())
                        .setBridge(false)
                        .setIsQos(true)
                        .setIsAckExtra(true)
                        .setToId(toId)
                        .setFromId(fromId))
                .setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.TextMessageType)
                        .setTextMessage(VitalPB.TextMessage.newBuilder()
                                .setContent(message)));

        return builder.build();
    }



    public static VitalPB.Protocol createHeartBeat() {
        VitalPB.Protocol.Builder builder = VitalPB.Protocol.newBuilder();

        return builder.build();
    }


    private static String getOneUUID() {
        return UUID.randomUUID().toString();
    }
}
