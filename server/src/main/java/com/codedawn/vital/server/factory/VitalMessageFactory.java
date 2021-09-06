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

    public static VitalPB.Frame createAck(VitalPB.Frame frame) {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
        builder.setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.AckMessageType)
                        .setAckMessage(VitalPB.AckMessage.newBuilder()
                                .setAckSeq(frame.getHeader().getSeq())))
                .setHeader(VitalPB.Header.newBuilder()
                        .setBridge(false)
                        .setIsQos(false)
                        .setSeq(getOneUUID()));
        return builder.build();
    }

    public static VitalPB.Frame createAckWithExtra(VitalPB.Frame frame, String perId, long timeStamp) {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
        builder.setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.AckMessageType)
                        .setAckMessage(VitalPB.AckMessage.newBuilder()
                                .setAckSeq(frame.getHeader().getSeq())))
                .setHeader(VitalPB.Header.newBuilder()
                        .setBridge(false)
                        .setIsQos(false)
                        .setSeq(getOneUUID())
                        .setTimestamp(timeStamp)
                        .setPerId(perId));
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

    public static VitalPB.Frame createAuthSuccess(String seq) {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
        builder.setHeader(VitalPB.Header.newBuilder()
                        .setSeq(getOneUUID())
                        .setBridge(false)
                        .setIsQos(true)
                        .setIsAckExtra(false))
                .setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.AuthSuccessMessageType)
                        .setAuthSuccessMessage(VitalPB.AuthSuccessMessage.newBuilder()
                                .setAckSeq(seq)));
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

    public static VitalPB.Frame createDisAuth() {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
        builder.setHeader(VitalPB.Header.newBuilder()
                        .setSeq(getOneUUID())
                        .setBridge(false)
                        .setIsQos(true)
                        .setIsAckExtra(false))
                .setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.DisAuthMessageType)
                        .setDisAuthMessage(VitalPB.DisAuthMessage.newBuilder()));

        return builder.build();
    }

//    public static VitalPB.Frame createDisAuthSuccess(String id) {
//        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
//        builder.setHeader(VitalPB.Header.newBuilder()
//                        .setSeq(getOneUUID())
//                        .setBridge(false)
//                        .setIsQos(true)
//                        .setIsAckExtra(false))
//                .setBody(VitalPB.Body.newBuilder()
//                        .setMessageType(VitalPB.MessageType.DisAuthSuccessMessageType)
//                        .setDisAuthSuccessMessage(VitalPB.DisAuthSuccessMessage.newBuilder()
//                                .setId(id)));
//
//        return builder.build();
//    }




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

    public static VitalPB.Frame createKickoutMessage() {
        VitalPB.Frame.Builder builder = VitalPB.Frame.newBuilder();
        builder.setHeader(VitalPB.Header.newBuilder()
                        .setBridge(false)
                        .setIsQos(false)
                        .setIsAckExtra(false))
                .setBody(VitalPB.Body.newBuilder()
                        .setMessageType(VitalPB.MessageType.KickoutMessageType)
                        .setKickoutMessage(VitalPB.KickoutMessage.newBuilder()));

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
