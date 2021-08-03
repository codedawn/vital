package com.codedean.vital.demo;

import com.codedawn.vital.server.VitalServer;

/**
 * @author codedawn
 * @date 2021-08-03 17:52
 */
public class VitalTCPServerDemo {
    public static void main(String[] args) {
        VitalServer vitalServer = new VitalServer();
        vitalServer.start();
    }
}
