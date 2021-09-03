package com.codedawn.vital.server;

import com.codedawn.vital.server.config.VitalGenericOption;
import com.codedawn.vital.server.config.VitalOption;

/**
 * @author codedawn
 * @date 2021-07-21 14:21
 */
public class VitalS {

    private TCPServer tcpServer = new TCPServer();

    public VitalS() {
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
        VitalS vitalS = new VitalS();
        TCPServer tcpServer = vitalS.getTcpServer();

        vitalS.start();
    }

    public TCPServer getTcpServer() {
        return tcpServer;
    }

    /**
     * 修改框架参数配置{@link VitalGenericOption}
     *
     * @param option
     * @param value
     * @param <T>
     * @return
     */
    public <T> void option(VitalOption<T> option, T value) {
        VitalGenericOption.option(option, value);
    }
}
