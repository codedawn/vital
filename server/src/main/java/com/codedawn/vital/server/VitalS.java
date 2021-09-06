package com.codedawn.vital.server;

import com.codedawn.vital.server.config.VitalGenericOption;
import com.codedawn.vital.server.config.VitalOption;
import com.codedawn.vital.server.processor.Processor;

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


    /**
     * 会覆盖默认设置，使用者的设置优先,command为key，只会存在一个对应value
     */
    public void registerProcessor(String command, Processor processor) {
        tcpServer.registerProcessor(command, processor);
    }

    /**
     * 注意与{@link VitalS#registerProcessor(String, Processor)}不同，userProcessor如果用户不设置，是没有默认的
     *
     * @param command
     * @param processor
     * @return
     */
    public void registerUserProcessor(String command, Processor processor) {
        tcpServer.registerUserProcessor(command, processor);
    }
}
