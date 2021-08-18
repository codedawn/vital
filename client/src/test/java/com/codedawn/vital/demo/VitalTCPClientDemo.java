package com.codedawn.vital.demo;


import com.codedawn.vital.client.TCPClient;
import com.codedawn.vital.client.VitalClient;
import com.codedawn.vital.config.VitalGenericOption;

/**
 * @author codedawn
 * @date 2021-08-03 17:53
 */
public class VitalTCPClientDemo {
    public static void main(String[] args) throws InterruptedException {
        VitalClient vitalClient = new VitalClient();
        TCPClient tcpClient = vitalClient.getTcpClient();
        tcpClient.option(VitalGenericOption.ID, String.valueOf("test"));
        vitalClient.start();



    }
}
