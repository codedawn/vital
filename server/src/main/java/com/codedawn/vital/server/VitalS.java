package com.codedawn.vital.server;

import com.codedawn.vital.server.config.VitalGenericOption;
import com.codedawn.vital.server.config.VitalOption;
import com.codedawn.vital.server.logic.AuthLogic;
import com.codedawn.vital.server.logic.ClusterLogic;
import com.codedawn.vital.server.logic.TransmitLogic;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.session.ConnectionEventProcessor;
import com.codedawn.vital.server.session.ConnectionEventType;

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

    /**
     * 设置tcp服务监听端口
     * @param port
     */
    public VitalS port(Integer port){
        option(VitalGenericOption.SERVER_TCP_PORT,port);
        return this;
    }

    /**
     * 设置是否开启集群部署
     * @param isCluster
     */
    public VitalS cluster(boolean isCluster){
        option(VitalGenericOption.CLUSTER,isCluster);
        return this;
    }

    public VitalS workId(int workId){
        option(VitalGenericOption.WORK_ID,workId);
        return this;
    }

    public VitalS dataCenterId(int dataCenterId){
        option(VitalGenericOption.DATA_CENTER_ID,dataCenterId);
        return this;
    }

    /**
     * 设置集群部署监听端口，集群默认实现是grpc，监听clusterPort
     * @param clusterPort
     */
    public VitalS clusterPort(Integer clusterPort){
        option(VitalGenericOption.CLUSTER_PORT,clusterPort);
        return this;
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


//    /**
//     * 会覆盖默认设置，使用者的设置优先,command为key，只会存在一个对应value
//     */
//    public void registerProcessor(String command, Processor processor) {
//        tcpServer.registerProcessor(command, processor);
//    }

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

    /**
     * 添加连接事件处理器，处理器不会覆盖。{@link ConnectionEventType}
     * @param eventType
     * @param connectionEventProcessor
     */
    public void addConnectionEventProcessor(ConnectionEventType eventType, ConnectionEventProcessor connectionEventProcessor){
        tcpServer.addConnectionEventProcessor(eventType,connectionEventProcessor);
    }



    /**
     * 设置登录逻辑
     * @param authLogic
     * @return
     */
    public void setAuthLogic(AuthLogic authLogic) {
        tcpServer.setAuthLogic(authLogic);
    }


    /**
     * 开启集群，需要设置clusterLogic
     * @param clusterLogic
     */
    public void setClusterLogic(ClusterLogic clusterLogic) {
        tcpServer.setClusterLogic(clusterLogic);
    }

    /**
     * 设置消息转发策略接口
     * @param transmitLogic
     */
    public void setTransmitLogic(TransmitLogic transmitLogic) {
        tcpServer.setTransmitLogic(transmitLogic);
    }
}
