package com.codedawn.vital.client;

/**
 * @author codedawn
 * @date 2021-07-24 11:02
 */
public class VitalClient {
    private TCPClient tcpClient = new TCPClient();

    /**
     * 启动客户端
     */
    public void start() {
        tcpClient.start();
    }
    /**
     * 关闭客户端
     */
    public void shutdown() {
        tcpClient.shutdown();
    }

    /**
     * 获取tcpClient进行TCP通信的配置
     */
    public TCPClient getTcpClient() {
        return tcpClient;
    }

    public static void main(String[] args) {
        VitalClient vitalClient = new VitalClient();
    }
}
