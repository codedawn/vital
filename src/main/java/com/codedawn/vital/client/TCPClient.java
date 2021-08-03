package com.codedawn.vital.client;

import com.codedawn.vital.callback.MessageCallBack;
import com.codedawn.vital.command.ClientDefaultCommandHandler;
import com.codedawn.vital.config.VitalGenericOption;
import com.codedawn.vital.config.VitalOption;
import com.codedawn.vital.connector.Sender;
import com.codedawn.vital.connector.TCPConnect;
import com.codedawn.vital.connector.VitalSender;
import com.codedawn.vital.processor.Processor;
import com.codedawn.vital.processor.ProcessorManager;
import com.codedawn.vital.processor.impl.client.AuthSuccessProcessor;
import com.codedawn.vital.processor.impl.client.DisAuthFinishProcessor;
import com.codedawn.vital.processor.impl.client.ExceptionProcessor;
import com.codedawn.vital.proto.Protocol;
import com.codedawn.vital.proto.ProtocolManager;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.proto.VitalTCPProtocol;
import com.codedawn.vital.qos.HeartBeatLauncher;
import com.codedawn.vital.qos.ReceiveQos;
import com.codedawn.vital.qos.SendQos;
import com.codedawn.vital.session.ConnectionEventListener;
import com.codedawn.vital.session.ConnectionEventType;
import com.codedawn.vital.session.impl.ClientConnectEventProcessor;
import com.codedawn.vital.session.impl.ClientDisconnectEventProcessor;

/**
 * @author codedawn
 * @date 2021-07-26 22:17
 */
public class TCPClient {

    private Class<? extends Protocol> protocolClass= null;

    private ProtocolManager protocolManager=new ProtocolManager();

    private TCPConnect tcpConnect ;

    private static ReceiveQos receiveQos = new ReceiveQos();

    private static SendQos sendQos = new SendQos();


    private ConnectionEventListener connectionEventListener = new ConnectionEventListener();

    private ProcessorManager processorManager = new ProcessorManager();

    public static Sender sender=new VitalSender(sendQos);

    private HeartBeatLauncher heartBeatLauncher=new HeartBeatLauncher(tcpConnect);

    private AuthSuccessProcessor authSuccessProcessor = null;

    private ExceptionProcessor exceptionProcessor = null;

    private DisAuthFinishProcessor disAuthFinishProcessor=null;

    private MessageCallBack messageCallBack = null;
    public TCPClient() {
        preInit();
    }

    /**
     * 这些初始化先于使用者的设置，使用者设置有可能覆盖
     */
    private void preInit() {

    }

    /**
     * 使用者没有进行初始化使用默认设置
     */
    private void afterInit() {


        if (protocolClass == null) {
            protocolClass = VitalTCPProtocol.class;
            protocolManager.registerProtocol(protocolClass.getSimpleName(),new VitalTCPProtocol(new ClientDefaultCommandHandler(processorManager,receiveQos,sendQos, null,sender,messageCallBack)));
        }

        tcpConnect = new TCPConnect(protocolClass, protocolManager,connectionEventListener);
        //设置心跳发射器
        tcpConnect.setHeartBeatLauncher(heartBeatLauncher);

        sender.setTcpConnect(tcpConnect);



        if (authSuccessProcessor == null) {
            authSuccessProcessor = new AuthSuccessProcessor(tcpConnect);
        }
        if (exceptionProcessor == null) {
            exceptionProcessor = new ExceptionProcessor(sender);
        }
        if (disAuthFinishProcessor == null) {
            disAuthFinishProcessor = new DisAuthFinishProcessor(tcpConnect);
        }
        processorManager.registerProcessor(VitalProtobuf.DataType.AuthSuccessMessageType.toString(),authSuccessProcessor);
        processorManager.registerProcessor(VitalProtobuf.DataType.ExceptionMessageType.toString(),exceptionProcessor);
        processorManager.registerProcessor(VitalProtobuf.DataType.DisAuthFinishMessageType.toString(),disAuthFinishProcessor);

        connectionEventListener.addConnectionEventProcessor(ConnectionEventType.CONNECT,new ClientConnectEventProcessor());
        connectionEventListener.addConnectionEventProcessor(ConnectionEventType.CLOSE,new ClientDisconnectEventProcessor(tcpConnect));
    }

    /**
     * 修改框架参数配置{@link VitalGenericOption}
     * @param option
     * @param value
     * @param <T>
     * @return
     */
    public  <T> TCPClient option(VitalOption<T> option, T value){
        VitalGenericOption.option(option, value);
        return this;
    }

    /**
     * 启动TCPClient
     */
    public void start() {
        afterInit();
        receiveQos.start();
        sendQos.start();
        tcpConnect.start();
    }

    /**
     * 注册protocol
     * @param name 协议类的class的simpleName
     * @param protocol
     */
    public TCPClient registerProtocol(String name, Protocol protocol) {
        protocolManager.registerProtocol(name,protocol);
        return this;
    }
    /**
     * 设置protocol类的class，框架将使用该协议，会覆盖默认设置，使用者的设置优先
     * @param protocolClass
     * @return
     */
    public TCPClient setProtocolClass(Class<? extends Protocol> protocolClass) {
        this.protocolClass = protocolClass;
        return this;
    }
    /**
     * 消息到达，通知回调，虽然可以通过设置processor处理不同类型的消息，但是如果需要处理所有类型消息，就需要都设置processor，相对比较麻烦，所有提供了该回调
     * @param messageCallBack
     */
    public TCPClient setMessageCallBack(MessageCallBack messageCallBack) {
        this.messageCallBack = messageCallBack;
        return this;
    }

    /**
     * 会覆盖默认设置，使用者的设置优先,command为key，只会存在一个对应value
     * @param command
     * @param processor
     * @return
     */
    public TCPClient registerProcessor(String command, Processor processor) {
        processorManager.registerProcessor(command, processor);
        return this;
    }

    public TCPClient setAuthSuccessProcessor(AuthSuccessProcessor authSuccessProcessor) {
        this.authSuccessProcessor = authSuccessProcessor;
        return this;
    }

    public TCPClient setExceptionProcessor(ExceptionProcessor exceptionProcessor) {
        this.exceptionProcessor = exceptionProcessor;
        return this;
    }

    public TCPClient setDisAuthFinishProcessor(DisAuthFinishProcessor disAuthFinishProcessor) {
        this.disAuthFinishProcessor = disAuthFinishProcessor;
        return this;
    }

    public Class<? extends Protocol> getProtocolClass() {
        return protocolClass;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public TCPConnect getTcpConnect() {
        return tcpConnect;
    }

    public ReceiveQos getReceiveQos() {
        return receiveQos;
    }

    public SendQos getSendQos() {
        return sendQos;
    }

    public ConnectionEventListener getConnectionEventListener() {
        return connectionEventListener;
    }

    public ProcessorManager getProcessorManager() {
        return processorManager;
    }

    public static Sender getSender() {
        return sender;
    }

    public HeartBeatLauncher getHeartBeatLauncher() {
        return heartBeatLauncher;
    }

    public AuthSuccessProcessor getAuthSuccessProcessor() {
        return authSuccessProcessor;
    }

    public ExceptionProcessor getExceptionProcessor() {
        return exceptionProcessor;
    }

    public DisAuthFinishProcessor getDisAuthFinishProcessor() {
        return disAuthFinishProcessor;
    }
}
