package com.codedawn.vital.client.factory;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.server.proto.VitalProtobuf;

import java.util.UUID;

/**
 * VitalMessageFactory工厂类
 * @author codedawn
 * @date 2021-07-25 22:59
 */
public class ClientVitalMessageFactory {

    public static VitalProtobuf.Protocol createAck(VitalProtobuf.Protocol message) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setMessageType(VitalProtobuf.MessageType.AckMessageType)
                .setBridge(false)
                .setQos(false)
                .setQosId(getOneUUID())
                .setAckMessage(VitalProtobuf.AckMessage.newBuilder()
                        .setAckQosId(message.getQosId()));
        return builder.build();
    }

    public static VitalProtobuf.Protocol createAckWithExtra(VitalProtobuf.Protocol message,String id,long timeStamp) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setMessageType(VitalProtobuf.MessageType.AckMessageWithExtraType)
                .setBridge(false)
                .setQos(false)
                .setQosId(getOneUUID())
                .setAckMessageWithExtra(VitalProtobuf.AckMessageWithExtra.newBuilder()
                        .setAckQosId(message.getQosId())
                        .setAckPerId(id)
                        .setAckTimeStamp(timeStamp)
                );
        return builder.build();
    }

    public static VitalProtobuf.Protocol createAuth(String id,String token) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setMessageType(VitalProtobuf.MessageType.AuthMessageType)
                .setQos(true)
                .setQosId(getOneUUID())
                .setBridge(false)
                .setAckExtra(false)
                .setAuthMessage(
                        VitalProtobuf.AuthMessage.newBuilder()
                                .setId(id)
                                .setToken(token));
        return builder.build();
    }



    public static VitalProtobuf.Protocol createException(String qosId,String extra,int code) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setMessageType(VitalProtobuf.MessageType.ExceptionMessageType)
                .setQos(true)
                .setQosId(getOneUUID())
                .setBridge(false)
                .setAckExtra(false)
                .setExceptionMessage(
                        VitalProtobuf.ExceptionMessage.newBuilder()
                                .setExceptionQosId(qosId)
                                .setExtra(extra)
                                .setCode(code)
                );

        return builder.build();
    }

    public static VitalProtobuf.Protocol createDisAuth(String id) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setMessageType(VitalProtobuf.MessageType.DisAuthMessageType)
                .setQos(true)
                .setQosId(getOneUUID())
                .setBridge(false)
                .setAckExtra(false)
                .setDisAuthMessage(
                        VitalProtobuf.DisAuthMessage.newBuilder()
                                .setId(id)

                );

        return builder.build();
    }



    public static VitalProtobuf.Protocol createTextMessage(String toId, String message) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setMessageType(VitalProtobuf.MessageType.TextMessageType)
                .setQos(true)
                .setQosId(getOneUUID())
                .setBridge(false)
                .setAckExtra(false)
                .setTextMessage(
                        VitalProtobuf.TextMessage.newBuilder()
                                .setFromId(ClientVitalGenericOption.ID.value())
                                .setToId(toId)
                                .setContent(message)
                );

        return builder.build();
    }



    public static VitalProtobuf.Protocol createHeartBeat() {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setMessageType(VitalProtobuf.MessageType.HeartbeatType);

        return builder.build();
    }

    private static String getOneUUID() {
        return UUID.randomUUID().toString();
    }
}
