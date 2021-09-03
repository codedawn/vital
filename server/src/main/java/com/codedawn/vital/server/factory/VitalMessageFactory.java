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

    public static VitalPB.Frame createAck(VitalPB.Frame message) {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
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

    public static VitalPB.Frame createAckWithExtra(VitalPB.Frame message, String perId, long timeStamp) {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
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

    public static VitalPB.Frame createAuthRequest(String id, String token) {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
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

    public static VitalPB.Frame createAuthSuccess(String id) {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
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

    public static VitalPB.Frame createException(String seq, String extra, int code) {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
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
    public static VitalPB.Frame createDisAuth(String id) {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
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

    public static VitalPB.Frame createDisAuthSuccess(String id) {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
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




    public static VitalPB.Frame createTextMessage(String fromId,String toId, String message) {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
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



    public static VitalPB.Frame createHeartBeat() {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();

        return builder.build();
    }


    private static String getOneUUID() {
        return UUID.randomUUID().toString();
    }
}
