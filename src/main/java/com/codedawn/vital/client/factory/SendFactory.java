package com.codedawn.vital.client.factory;

import com.codedawn.vital.client.config.Info;

import java.util.UUID;

/**
 * @author codedawn
 * @date 2021-07-25 8:44
 */
public class SendFactory {

    private SendFactory() {

    }
//    private static SendFactory instance;
//
//    public static SendFactory getInstance() {
//        if (instance == null) {
//            instance = new SendFactory();
//        }
//        return instance;
//    }

    public static VitalProtocol.Protocol createAuth() {
        VitalProtocol.Protocol.Builder builder = VitalProtocol.Protocol.newBuilder();
        builder.setDataType(VitalProtocol.DataType.AuthMessageType)
                .setQos(true)
                .setQosId(getOneUUID())
                .setBridge(false)
                .setAuthMessage(
                        VitalProtocol.AuthMessage.newBuilder()
                        .setId(Info.id)
                        .setToken(Info.token));
        return builder.build();
    }

    private static String getOneUUID() {
        return UUID.randomUUID().toString();
    }
}
