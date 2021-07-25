package com.codedawn.vital.factory;

import com.codedawn.vital.client.config.Info;
import com.codedawn.vital.proto.VitalProtobuf;

import java.util.UUID;

/**
 * @author codedawn
 * @date 2021-07-25 22:59
 */
public class VitalMessageFactory {

    public static VitalProtobuf.Protocol createAck(VitalProtobuf.Protocol message) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setDataType(VitalProtobuf.DataType.AckMessageType)
                .setBridge(false)
                .setQos(false)
                .setAckMessage(VitalProtobuf.AckMessage.newBuilder()
                        .setAckQosId(message.getQosId()));
        return builder.build();
    }

    public static VitalProtobuf.Protocol createAckWithExtra(VitalProtobuf.Protocol message,String id,long timeStamp) {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setDataType(VitalProtobuf.DataType.AckMessageWithExtraType)
                .setBridge(false)
                .setQos(false)
                .setAckMessageWithExtra(VitalProtobuf.AckMessageWithExtra.newBuilder()
                        .setAckQosId(message.getQosId())
                        .setAckPerId(id)
                        .setAckTimeStamp(timeStamp)
                );
        return builder.build();
    }

    public static VitalProtobuf.Protocol createAuth() {
        VitalProtobuf.Protocol.Builder builder = VitalProtobuf.Protocol.newBuilder();
        builder.setDataType(VitalProtobuf.DataType.AuthMessageType)
                .setQos(true)
                .setQosId(getOneUUID())
                .setBridge(false)
                .setAckExtra(false)
                .setAuthMessage(
                        VitalProtobuf.AuthMessage.newBuilder()
                                .setId(Info.id)
                                .setToken(Info.token));
        return builder.build();
    }

    private static String getOneUUID() {
        return UUID.randomUUID().toString();
    }
}
