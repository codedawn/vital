package com.codedawn.vital.demo;


import com.codedawn.vital.client.TCPClient;
import com.codedawn.vital.client.VitalClient;
import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.client.connector.Sender;
import com.codedawn.vital.client.factory.ClientVitalMessageFactory;

/**
 * @author codedawn
 * @date 2021-08-03 17:53
 */
public class VitalTCPClientDemo {
    public static void main(String[] args) throws InterruptedException {
        VitalClient vitalClient = new VitalClient();
        TCPClient tcpClient = vitalClient.getTcpClient();
        tcpClient.option(ClientVitalGenericOption.ID, String.valueOf("test"));
        vitalClient.start();

        Sender sender = vitalClient.getTcpClient().getSender();
        while (true) {
            Thread.sleep(10);
            sender.send(ClientVitalMessageFactory.createCommonMessage("123", "hello"));
        }


    }
}
