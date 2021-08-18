package com.codedawn.vital.server;

/**
 * @author codedawn
 * @date 2021-07-21 14:21
 */
public class VitalServer {

    private TCPServer tcpServer = new TCPServer();

    public VitalServer() {
    }

    /**
     * 启动
     */
    public void start() {
        tcpServer.start();
    }

    /**
     * 关闭
     */
    public void shutdown() {
        tcpServer.shutdown();
    }
    public static void main(String[] args) {
        VitalServer vitalServer = new VitalServer();
        TCPServer tcpServer = vitalServer.getTcpServer();

        vitalServer.start();
    }

    public TCPServer getTcpServer() {
        return tcpServer;
    }
}
