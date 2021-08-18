package com.codedawn.vital.demo;


import com.codedawn.vital.server.VitalServer;

/**
 * @author codedawn
 * @date 2021-08-03 17:52
 */
public class VitalTCPServerDemo {
    public static void main(String[] args) throws InterruptedException {
        VitalServer vitalServer = new VitalServer();
        vitalServer.start();
//        Thread.sleep(5000);
//        vitalServer.shutdown();
    }
}
