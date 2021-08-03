package com.codedawn.vital.client;

/**
 * @author codedawn
 * @date 2021-07-24 11:02
 */
public class VitalClient {
    private TCPClient tcpClient = new TCPClient();

    public void start() {
        tcpClient.start();
    }

    public TCPClient getTcpClient() {
        return tcpClient;
    }

    public static void main(String[] args) {
        VitalClient vitalClient = new VitalClient();
    }
}
