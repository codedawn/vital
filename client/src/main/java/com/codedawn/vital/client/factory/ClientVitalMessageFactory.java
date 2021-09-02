package com.codedawn.vital.client.factory;

import java.util.UUID;

/**
 * VitalMessageFactory工厂类
 * @author codedawn
 * @date 2021-07-25 22:59
 */
public class ClientVitalMessageFactory {





    private static String getOneUUID() {
        return UUID.randomUUID().toString();
    }
}
