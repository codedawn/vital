package com.codedawn.vital.server;

import com.codedawn.vital.server.callback.MessageCallBack;
import com.codedawn.vital.server.callback.TimeoutMessageCallBack;
import com.codedawn.vital.server.command.ServerDefaultCommandHandler;
import com.codedawn.vital.server.config.VitalGenericOption;
import com.codedawn.vital.server.config.VitalOption;
import com.codedawn.vital.server.connector.TCPConnector;
import com.codedawn.vital.server.connector.VitalSendHelper;
import com.codedawn.vital.server.factory.VitalMessageFactory;
import com.codedawn.vital.server.factory.impl.SnowFlakeSeqStrategy;
import com.codedawn.vital.server.logic.ClusterLogic;
import com.codedawn.vital.server.logic.OfflineMessageLogic;
import com.codedawn.vital.server.logic.TransmitLogic;
import com.codedawn.vital.server.logic.AuthLogic;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.processor.ProcessorManager;
import com.codedawn.vital.server.processor.impl.server.AuthProcessor;
import com.codedawn.vital.server.processor.impl.server.DisAuthProcessor;
import com.codedawn.vital.server.processor.impl.server.ImageMessageProcessor;
import com.codedawn.vital.server.processor.impl.server.TextMessageProcessor;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.ProtocolManager;
import com.codedawn.vital.server.proto.VitalPB;
import com.codedawn.vital.server.proto.VitalProtocol;
import com.codedawn.vital.server.qos.ReceiveQos;
import com.codedawn.vital.server.qos.SendQos;
import com.codedawn.vital.server.rpc.ClusterProcessor;
import com.codedawn.vital.server.rpc.RpcClient;
import com.codedawn.vital.server.rpc.RpcServer;
import com.codedawn.vital.server.session.ConnectionEventListener;
import com.codedawn.vital.server.session.ConnectionEventProcessor;
import com.codedawn.vital.server.session.ConnectionEventType;
import com.codedawn.vital.server.session.ConnectionManage;
import com.codedawn.vital.server.session.impl.ConnectEventProcessor;
import com.codedawn.vital.server.session.impl.DisconnectEventProcessor;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author codedawn
 * @date 2021-07-21 9:38
 */
public class TCPServer {

    private static Logger log = LoggerFactory.getLogger(TCPServer.class);


    private ReceiveQos receiveQos;

    private SendQos sendQos;


    private ProtocolManager protocolManager;

    private Class<? extends Protocol> protocolClass;

    private Protocol protocol;

    private VitalSendHelper vitalSendHelper;


    private ProcessorManager processorManager;

    private ProcessorManager userProcessorManager;


    private ConnectionManage connectionManage;

    private ConnectionEventListener connectionEventListener;


    private TCPConnector tcpConnector;


    private AuthProcessor authProcessor;

    private TextMessageProcessor textMessageProcessor;

    private ImageMessageProcessor imageMessageProcessor;

    private DisAuthProcessor disAuthProcessor;

    private TransmitLogic transmitLogic;

    private MessageCallBack messageCallBack;

    private TimeoutMessageCallBack timeoutMessageCallBack;

    private ClusterProcessor clusterProcessor;

    private RpcServer rpcServer;

    public TCPServer() {
        preInit();
    }

    /**
     * ????????????
     */
    private void DI() {
        //???????????????????????????????????????seq
        VitalMessageFactory.setSeqStrategy(new SnowFlakeSeqStrategy());

        receiveQos.setSendQos(sendQos);
        sendQos.setProtocol(protocol).setTimeoutMessageCallBack(timeoutMessageCallBack);

        if (protocol instanceof VitalProtocol) {
            VitalProtocol vitalProtocol = (VitalProtocol) protocol;

            ServerDefaultCommandHandler serverDefaultCommandHandler = new ServerDefaultCommandHandler();


            vitalProtocol
                    .setDecode(new ProtobufDecoder(VitalPB.Frame.getDefaultInstance()))
                    .setEncode(new ProtobufEncoder())
                    .setVitalSendHelper(vitalSendHelper)
                    .setCommandHandler(serverDefaultCommandHandler);
            serverDefaultCommandHandler
                    .setProtocol(vitalProtocol)
                    .setReceiveQos(receiveQos)
                    .setSendQos(sendQos)
                    .setProcessorManager(processorManager)
                    .setUserProcessorManager(userProcessorManager)
                    .setMessageCallBack(messageCallBack);

            vitalSendHelper
                    .setSendQos(sendQos)
                    .setConnectionManage(connectionManage)
                    .setClusterProcessor(clusterProcessor);

        }
        connectionManage.setProtocol(protocol);

        tcpConnector
                .setConnectionEventListener(connectionEventListener)
                .setProtocolManager(protocolManager)
                .setProtocolClass(protocolClass);

        authProcessor.setProtocol(protocol);
        textMessageProcessor.setProtocol(protocol).setTransmitLogic(transmitLogic);
        imageMessageProcessor.setProtocol(protocol).setTransmitLogic(transmitLogic);
        disAuthProcessor.setProtocol(protocol);

        processorManager.registerProcessor(VitalPB.MessageType.AuthRequestMessageType.name(), authProcessor);
        processorManager.registerProcessor(VitalPB.MessageType.TextMessageType.name(), textMessageProcessor);
        processorManager.registerProcessor(VitalPB.MessageType.ImageMessageType.name(), imageMessageProcessor);
        processorManager.registerProcessor(VitalPB.MessageType.DisAuthMessageType.name(), disAuthProcessor);

        clusterProcessor.setRpcClient(new RpcClient());
        rpcServer.setProtocol(protocol);
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     */
    private void preInit() {
        initQos();
        initProtocol();
        initProcessorManager();
        initEventProcessor();
        initTcp();
        initCallBack();
        initMessageProcessor();
        initCluster();
    }


    /**
     * ?????????qos
     */
    private void initQos() {
        this.receiveQos = new ReceiveQos();
        this.sendQos = new SendQos();
    }

    /**
     * ???????????????
     */
    private void initProtocol() {
        this.protocolManager = new ProtocolManager();
        this.protocolClass = VitalProtocol.class;
        this.protocol = new VitalProtocol();
        this.vitalSendHelper = new VitalSendHelper();

        protocolManager.registerProtocol(protocolClass.getSimpleName(), protocol);
    }

    /**
     * ?????????ProcessorManager???processorManager???userProcessorManager
     */
    private void initProcessorManager() {
        this.processorManager = new ProcessorManager(
                new ThreadPoolExecutor(VitalGenericOption.PROCESSOR_MIN_POOlSIZE.value(),
                        VitalGenericOption.PROCESSOR_MAX_POOlSIZE.value(),
                        VitalGenericOption.PROCESSOR_KEEP_ALIVE_TIME.value(),
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(VitalGenericOption.PROCESSOR_QUEUE_SIZE.value()),
                        new DefaultThreadFactory("vital-processor-executor", true),
                        new ThreadPoolExecutor.AbortPolicy())
        );

        this.userProcessorManager = new ProcessorManager(
                new ThreadPoolExecutor(VitalGenericOption.USER_PROCESSOR_MIN_POOlSIZE.value(),
                        VitalGenericOption.USER_PROCESSOR_MAX_POOlSIZE.value(),
                        VitalGenericOption.USER_PROCESSOR_KEEP_ALIVE_TIME.value(),
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>(VitalGenericOption.USER_PROCESSOR_QUEUE_SIZE.value()),
                        new DefaultThreadFactory("vital-user-processor-executor", true),
                        new ThreadPoolExecutor.AbortPolicy())
        );
    }

    /**
     * ?????????EventProcessor????????????????????????
     */
    private void initEventProcessor() {
        this.connectionManage = new ConnectionManage();
        this.connectionEventListener = new ConnectionEventListener();
        this.connectionEventListener.addConnectionEventProcessor(ConnectionEventType.CONNECT, new ConnectEventProcessor(connectionManage));
        this.connectionEventListener.addConnectionEventProcessor(ConnectionEventType.CLOSE, new DisconnectEventProcessor(connectionManage));
    }

    /**
     * ?????????tcp????????????
     */
    private void initTcp() {
        this.tcpConnector = new TCPConnector();
    }

    /**
     * ???????????????
     */
    private void initCallBack() {

    }

    private void initMessageProcessor() {

        transmitLogic = new TransmitLogic();

        authProcessor = new AuthProcessor();
        textMessageProcessor = new TextMessageProcessor();
        imageMessageProcessor = new ImageMessageProcessor();
        disAuthProcessor = new DisAuthProcessor();

    }

    private void initCluster(){
        clusterProcessor = new ClusterProcessor();
        rpcServer = new RpcServer();
    }


    /**
     * ??????start?????????
     */
    private void afterInit() {
        DI();
    }


    public TCPServer setTimeoutMessageCallBack(TimeoutMessageCallBack timeoutMessageCallBack) {
        this.timeoutMessageCallBack = timeoutMessageCallBack;
        return this;
    }

//    /**
//     * ??????????????????????????????????????????????????????processor?????????????????????????????????????????????????????????????????????????????????????????????processor????????????????????????????????????????????????
//     *
//     * @param messageCallBack
//     */
//    public TCPServer setMessageCallBack(MessageCallBack messageCallBack) {
//        this.messageCallBack = messageCallBack;
//        return this;
//    }
//
//    public TCPServer setAuthProcessor(AuthProcessor authProcessor) {
//        this.authProcessor = authProcessor;
//        return this;
//    }
//
//    public TCPServer setCommonMessageProcessor(GeneralMessageProcessor generalMessageProcessor) {
//        this.generalMessageProcessor = generalMessageProcessor;
//        return this;
//    }
//
//    public TCPServer setDisAuthProcessor(DisAuthProcessor disAuthProcessor) {
//        this.disAuthProcessor = disAuthProcessor;
//        return this;
//    }


    /**
     * ????????????????????????{@link VitalGenericOption}
     *
     * @param option
     * @param value
     * @param <T>
     * @return
     */
    public <T> TCPServer option(VitalOption<T> option, T value) {
        VitalGenericOption.option(option, value);
        return this;
    }

    /**
     * ??????TCP?????????
     */
    public void start() {
        afterInit();
        receiveQos.start();
        sendQos.start();
        tcpConnector.start();

        rpcServer.start();
    }

    /**
     * ??????TCP?????????
     */
    public void shutdown() {
        receiveQos.shutdown();
        sendQos.shutdown();
        tcpConnector.shutdown();
        rpcServer.shutdown();
    }

    /**
     * ??????protocol
     *
     * @param name     ????????????class???simpleName
     * @param protocol
     */
    public TCPServer registerProtocol(String name, Protocol protocol) {
        protocolManager.registerProtocol(name, protocol);
        return this;
    }


    /**
     * ??????protocol??????class??????????????????????????????????????????????????????????????????????????????
     *
     * @param protocolClass
     * @return
     */
    public TCPServer setProtocolClass(Class<? extends Protocol> protocolClass) {
        this.protocolClass = protocolClass;
        return this;
    }

    /**
     * ????????????????????????????????????????????????,command???key???????????????????????????value
     *
     * @param command
     * @param processor
     * @return
     */
    public TCPServer registerProcessor(String command, Processor processor) {
        processorManager.registerProcessor(command, processor);
        return this;
    }

    /**
     * ?????????{@link TCPServer#registerProcessor(String, Processor)}?????????userProcessor??????????????????????????????????????????
     *
     * @param command
     * @param processor
     * @return
     */
    public TCPServer registerUserProcessor(String command, Processor processor) {
        userProcessorManager.registerProcessor(command, processor);
        return this;
    }

    public TCPServer setAuthProcessor(AuthProcessor authProcessor) {
        this.authProcessor = authProcessor;
        return this;
    }

    /**
     * ??????????????????
     * @param authLogic
     * @return
     */
    public TCPServer setAuthLogic(AuthLogic authLogic) {
        this.authProcessor.setAuthLogic(authLogic);
        return this;
    }

    public TCPServer setTransmitLogic(TransmitLogic transmitLogic) {
        this.transmitLogic = transmitLogic;
        return this;
    }

    public TCPServer setOfflineMessageLogic(OfflineMessageLogic offlineMessageLogic) {
        this.vitalSendHelper.setOfflineMessageLogic(offlineMessageLogic);
        return this;
    }
    public TCPServer addConnectionEventProcessor(ConnectionEventType eventType, ConnectionEventProcessor connectionEventProcessor){
        this.connectionEventListener.addConnectionEventProcessor(eventType,connectionEventProcessor);
        return this;
    }

    public TCPServer setClusterProcessor(ClusterProcessor clusterProcessor) {
        this.clusterProcessor = clusterProcessor;
        return this;
    }

    public TCPServer setClusterLogic(ClusterLogic clusterLogic) {
        this.clusterProcessor.setClusterLogic(clusterLogic);
        return this;
    }




}
