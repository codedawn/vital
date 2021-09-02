package com.codedawn.vital.server;

import com.codedawn.vital.server.callback.MessageCallBack;
import com.codedawn.vital.server.callback.ResponseCallBack;
import com.codedawn.vital.server.command.ServerDefaultCommandHandler;
import com.codedawn.vital.server.config.VitalGenericOption;
import com.codedawn.vital.server.config.VitalOption;
import com.codedawn.vital.server.connector.TCPConnector;
import com.codedawn.vital.server.connector.VitalSendHelper;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.processor.ProcessorManager;
import com.codedawn.vital.server.processor.impl.server.AuthProcessor;
import com.codedawn.vital.server.processor.impl.server.DisAuthProcessor;
import com.codedawn.vital.server.processor.impl.server.GeneralMessageProcessor;
import com.codedawn.vital.server.proto.*;
import com.codedawn.vital.server.qos.ReceiveQos;
import com.codedawn.vital.server.qos.SendQos;
import com.codedawn.vital.server.session.ConnectionEventListener;
import com.codedawn.vital.server.session.ConnectionEventType;
import com.codedawn.vital.server.session.ConnectionManage;
import com.codedawn.vital.server.session.impl.ConnectEventProcessor;
import com.codedawn.vital.server.session.impl.DisconnectEventProcessor;
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


    private ReceiveQos receiveQos=new ReceiveQos();


    private ProtocolManager protocolManager=new ProtocolManager();

    private Class<? extends Protocol> protocolClass= null;


    private ProcessorManager processorManager=new ProcessorManager(
            new ThreadPoolExecutor(VitalGenericOption.PROCESSOR_MIN_POOlSIZE.value(),
                    VitalGenericOption.PROCESSOR_MAX_POOlSIZE.value(),
                    VitalGenericOption.PROCESSOR_KEEP_ALIVE_TIME.value(),
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(VitalGenericOption.PROCESSOR_QUEUE_SIZE.value()),
                    new DefaultThreadFactory("vital-processor-executor", true),
                    new ThreadPoolExecutor.DiscardPolicy())
    );

    private ProcessorManager userProcessorManager =new ProcessorManager(
            new ThreadPoolExecutor(VitalGenericOption.USER_PROCESSOR_MIN_POOlSIZE.value(),
                    VitalGenericOption.USER_PROCESSOR_MAX_POOlSIZE.value(),
                    VitalGenericOption.USER_PROCESSOR_KEEP_ALIVE_TIME.value(),
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(VitalGenericOption.USER_PROCESSOR_QUEUE_SIZE.value()),
                    new DefaultThreadFactory("vital-user-processor-executor", true),
                    new ThreadPoolExecutor.DiscardPolicy())
    );

    private ConnectionManage connectionManage=new ConnectionManage();

    private SendQos sendQos = new SendQos();

    private ConnectionEventListener connectionEventListener=new ConnectionEventListener();


    private TCPConnector tcpConnector;

    private ResponseCallBack responseCallBack;

    private AuthProcessor authProcessor=null;

    private GeneralMessageProcessor generalMessageProcessor;

    private DisAuthProcessor disAuthProcessor;


    private MessageCallBack messageCallBack=null;
    public TCPServer() {
        preInit();
    }

    /**
     * 这些初始化先于使用者的设置，使用者设置有可能覆盖
     */
    private void preInit() {

        connectionEventListener.addConnectionEventProcessor(ConnectionEventType.CONNECT,new ConnectEventProcessor(connectionManage));

        connectionEventListener.addConnectionEventProcessor(ConnectionEventType.CLOSE,new DisconnectEventProcessor(connectionManage));
    }

    /**
     * 使用者没有进行初始化使用默认设置
     */
    private void afterInit() {
        if (authProcessor == null) {
            authProcessor=new AuthProcessor(connectionManage, sendQos);
        }
        if (generalMessageProcessor == null) {
            generalMessageProcessor =new GeneralMessageProcessor();
        }
        if (disAuthProcessor == null) {
            disAuthProcessor=new DisAuthProcessor(connectionManage, sendQos);
        }

        processorManager.registerProcessor(VitalPB.MessageType.AuthRequestMessageType.toString(),authProcessor);
        processorManager.registerProcessor(VitalPB.MessageType.TextMessageType.toString(), generalMessageProcessor);
        processorManager.registerProcessor(VitalPB.MessageType.DisAuthMessageType.toString(),disAuthProcessor);

        if (protocolClass == null) {
            protocolClass = VitalProtocol.class;

            tcpConnector = new TCPConnector(protocolClass, protocolManager, connectionEventListener);
            ServerDefaultCommandHandler serverDefaultCommandHandler = new ServerDefaultCommandHandler(processorManager,userProcessorManager, receiveQos, sendQos, responseCallBack,messageCallBack);
            VitalProtocol vitalProtocol = new VitalProtocol(serverDefaultCommandHandler)
                    .setVitalSendHelper(new VitalSendHelper()
                        .setSendQos(sendQos)
                        .setConnectionManage(connectionManage));

            protocolManager.registerProtocol(protocolClass.getSimpleName(), vitalProtocol);

            serverDefaultCommandHandler.setProtocol(vitalProtocol);
            disAuthProcessor.setProtocol(vitalProtocol);
            authProcessor.setProtocol(vitalProtocol);
            generalMessageProcessor.setProtocol(vitalProtocol);

        }


    }

    /**
     * 消息到达，通知回调，虽然可以通过设置processor处理不同类型的消息，但是如果需要处理所有类型消息，就需要都设置processor，相对比较麻烦，所有提供了该回调
     * @param messageCallBack
     */
    public TCPServer setMessageCallBack(MessageCallBack messageCallBack) {
        this.messageCallBack = messageCallBack;
        return this;
    }

    public TCPServer setAuthProcessor(AuthProcessor authProcessor) {
        this.authProcessor = authProcessor;
        return this;
    }

    public TCPServer setCommonMessageProcessor(GeneralMessageProcessor generalMessageProcessor) {
        this.generalMessageProcessor = generalMessageProcessor;
        return this;
    }

    public TCPServer setDisAuthProcessor(DisAuthProcessor disAuthProcessor) {
        this.disAuthProcessor = disAuthProcessor;
        return this;
    }

    public AuthProcessor getAuthProcessor() {
        return authProcessor;
    }

    public GeneralMessageProcessor getCommonMessageProcessor() {
        return generalMessageProcessor;
    }

    public DisAuthProcessor getDisAuthProcessor() {
        return disAuthProcessor;
    }



    /**
     * 修改框架参数配置{@link VitalGenericOption}
     * @param option
     * @param value
     * @param <T>
     * @return
     */
    public  <T> TCPServer option(VitalOption<T> option, T value){
        VitalGenericOption.option(option, value);
        return this;
    }

    /**
     * 启动TCP服务器
     */
    public void start() {
        afterInit();
        receiveQos.start();
        sendQos.start();
        tcpConnector.start();
    }

    /**
     * 关闭TCP服务器
     */
    public void shutdown() {
        receiveQos.shutdown();
        sendQos.shutdown();
        tcpConnector.shutdown();
    }

    /**
     * 注册protocol
     * @param name 协议类的class的simpleName
     * @param protocol
     */
    public  TCPServer registerProtocol(String name,Protocol protocol) {
        protocolManager.registerProtocol(name,protocol);
        return this;
    }


    /**
     * 设置protocol类的class，框架将使用该协议，会覆盖默认设置，使用者的设置优先
     * @param protocolClass
     * @return
     */
    public TCPServer setProtocolClass(Class<? extends Protocol> protocolClass) {
        this.protocolClass = protocolClass;
        return this;
    }

    /**
     * 会覆盖默认设置，使用者的设置优先,command为key，只会存在一个对应value
     * @param command
     * @param processor
     * @return
     */
    public TCPServer registerProcessor(String command, Processor processor) {
        processorManager.registerProcessor(command, processor);
        return this;
    }

    /**
     * 注意于{@link TCPServer#registerProcessor(String, Processor)}不同，userProcessor如果用户不设置，是没有默认的
     * @param command
     * @param processor
     * @return
     */
    public TCPServer registerUserProcessor(String command, Processor processor) {
        userProcessorManager.registerProcessor(command, processor);
        return this;
    }

    /**
     * 服务器发出的所有启用qos消息的responseCallBack,启用了qos的消息，对方收到消息后会发出一个ack，ack到达服务器时，服务器调用该回调
     * @param responseCallBack
     * @return
     */
    public TCPServer setResponseCallBack(ResponseCallBack responseCallBack) {
        this.responseCallBack = responseCallBack;
        return this;
    }

    public static Logger getLog() {
        return log;
    }

    public ReceiveQos getReceiveQos() {
        return receiveQos;
    }

    public SendQos getSendQos() {
        return sendQos;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public Class<? extends Protocol> getProtocolClass() {
        return protocolClass;
    }

    public ProcessorManager getProcessorManager() {
        return processorManager;
    }

    public ConnectionManage getConnectionManage() {
        return connectionManage;
    }

    public ConnectionEventListener getConnectionEventListener() {
        return connectionEventListener;
    }

    public TCPConnector getTcpConnector() {
        return tcpConnector;
    }

    public ResponseCallBack getResponseCallBack() {
        return responseCallBack;
    }
}
