package com.codedawn.vital.demo;


import com.codedawn.vital.server.VitalS;

/**
 * @author codedawn
 * @date 2021-08-03 17:52
 */
public class VitalTCPServerDemo {
    public static void main(String[] args) throws InterruptedException {
        VitalS vitalS = new VitalS().cluster(true);
        vitalS.start();
//        Thread.sleep(5000);
//        vitalServer.shutdown();
    }
}
class VitalTCPServerDemo1 {
    public static void main(String[] args) throws InterruptedException {
        VitalS vitalS = new VitalS();
        vitalS.cluster(true)
                .clusterPort(9091)
                .port(9001);
        vitalS.start();
//        Thread.sleep(5000);
//        vitalServer.shutdown();
    }
}
