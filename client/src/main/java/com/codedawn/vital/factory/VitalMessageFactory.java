package com.codedawn.vital.factory;

import com.codedawn.vital.config.VitalGenericOption;
import com.codedawn.vital.proto.VitalProtobuf;

import java.util.UUID;

/**
 * VitalMessageFactory工厂类
 * @author codedawn
 * @date 2021-07-25 22:59
 */
public class VitalMessageFactory {

    public static VitalProtobuf.Protocol createAck(VitalProtobuf.Protocol message) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setDataType(VitalProtobuf.DataType.AckMessageType)
                .setBridge(false)
                .setQos(false)
                .setQosId(getOneUUID())
                .setAckMessage(VitalProtobuf.AckMessage.newBuilder()
                        .setAckQosId(message.getQosId()));
        return builder.build();
    }

    public static VitalProtobuf.Protocol createAckWithExtra(VitalProtobuf.Protocol message,String id,long timeStamp) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setDataType(VitalProtobuf.DataType.AckMessageWithExtraType)
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
        builder.setDataType(VitalProtobuf.DataType.AuthMessageType)
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

    public static VitalProtobuf.Protocol createAuthSuccess(String id) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setDataType(VitalProtobuf.DataType.AuthSuccessMessageType)
                .setQos(true)
                .setQosId(getOneUUID())
                .setBridge(false)
                .setAckExtra(false)
                .setAuthSuccessMessage(
                        VitalProtobuf.AuthSuccessMessage.newBuilder()
                                .setId(id));
        return builder.build();
    }

    public static VitalProtobuf.Protocol createException(String qosId,String extra,int code) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setDataType(VitalProtobuf.DataType.ExceptionMessageType)
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
        builder.setDataType(VitalProtobuf.DataType.DisAuthMessageType)
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

    public static VitalProtobuf.Protocol createDisAuthFinish(String id) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setDataType(VitalProtobuf.DataType.DisAuthFinishMessageType)
                .setQos(true)
                .setQosId(getOneUUID())
                .setBridge(false)
                .setAckExtra(false)
                .setDisAuthFinishMessage(
                        VitalProtobuf.DisAuthFinishMessage.newBuilder()
                                .setId(id)

                );

        return builder.build();
    }

    public static VitalProtobuf.Protocol createCommonMessage(String toId,String message) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setDataType(VitalProtobuf.DataType.CommonMessageType)
                .setQos(true)
                .setQosId(getOneUUID())
                .setBridge(false)
                .setAckExtra(false)
                .setCommonMessage(
                        VitalProtobuf.CommonMessage.newBuilder()
                                .setFromId(VitalGenericOption.ID.value())
                                .setToId(toId)
                                .setMessage(message)
                );

        return builder.build();
    }

    public static VitalProtobuf.Protocol createGroupMessage(String toId,String message) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setDataType(VitalProtobuf.DataType.GroupMessageType)
                .setQos(true)
                .setQosId(getOneUUID())
                .setBridge(false)
                .setAckExtra(false)
                .setGroupMessage(
                        VitalProtobuf.GroupMessage.newBuilder()
                                .setFromId(VitalGenericOption.ID.value())
                                .setToId(toId)
                                .setMessage(message)
                );

        return builder.build();
    }

    public static VitalProtobuf.Protocol createHeartBeat() {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setDataType(VitalProtobuf.DataType.HeartbeatType);

        return builder.build();
    }

    private static String getOneUUID() {
        return UUID.randomUUID().toString();
    }
}
