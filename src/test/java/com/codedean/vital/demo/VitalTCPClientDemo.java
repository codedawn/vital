package com.codedean.vital.demo;

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
//
//        Thread.sleep(3000);
//        VitalClient vitalClient1 = new VitalClient();
//
//        TCPClient tcpClient1 = vitalClient1.getTcpClient();
//        tcpClient1.option(VitalGenericOption.ID, String.valueOf("test1"));
//        tcpClient1.start();
//        for (int i = 1; i <= 1000; i++) {
//            Thread.sleep(2000);
//            VitalClient vitalClient = new VitalClient();
//            TCPClient tcpClient = vitalClient.getTcpClient();
//            tcpClient.option(VitalGenericOption.ID, String.valueOf(i+"test"));
//            vitalClient.start();
//            TCPClient.sender.send(VitalMessageFactory.createCommonMessage("555","这是一条消息"));
//        }




    }
}
