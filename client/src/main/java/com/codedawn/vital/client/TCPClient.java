package com.codedawn.vital.client;

import com.codedawn.vital.client.command.ClientDefaultCommandHandler;
import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.client.connector.Sender;
import com.codedawn.vital.client.connector.TCPConnect;
import com.codedawn.vital.client.connector.VitalSender;
import com.codedawn.vital.client.factory.IDSeqStrategy;
import com.codedawn.vital.client.processor.ClientProcessorManager;
import com.codedawn.vital.client.processor.impl.client.AuthSuccessProcessor;
import com.codedawn.vital.client.processor.impl.client.ExceptionProcessor;
import com.codedawn.vital.client.processor.impl.client.KickoutProcessor;
import com.codedawn.vital.client.qos.ClientReceiveQos;
import com.codedawn.vital.client.qos.ClientSendQos;
import com.codedawn.vital.client.qos.HeartBeatLauncher;
import com.codedawn.vital.client.session.ClientConnectionEventListener;
import com.codedawn.vital.client.session.impl.ClientConnectEventProcessor;
import com.codedawn.vital.client.session.impl.ClientDisconnectEventProcessor;
import com.codedawn.vital.server.callback.*;
import com.codedawn.vital.server.connector.VitalSendHelper;
import com.codedawn.vital.server.factory.VitalMessageFactory;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.*;
import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionEventType;
import io.netty.channel.Channel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.util.Attribute;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author codedawn
 * @date 2021-07-26 22:17
 */
public class TCPClient {

    private static Logger log = LoggerFactory.getLogger(TCPClient.class);

    private ClientReceiveQos clientReceiveQos;

    private ClientSendQos clientSendQos;

    private ExecutorService qosExecutor;

    private ProtocolManager protocolManager;

    private Class<? extends Protocol> protocolClass;

    private Protocol protocol;

    private ClientProcessorManager clientProcessorManager;

    private ClientProcessorManager clientUserProcessorManager;

    private ClientConnectionEventListener clientConnectionEventListener;

    private TCPConnect tcpConnect;

    public Sender sender;

    private HeartBeatLauncher heartBeatLauncher;

    private AuthSuccessProcessor authSuccessProcessor;

    private ExceptionProcessor exceptionProcessor;



    private KickoutProcessor kickoutProcessor;

    private ChannelStatusCallBack channelStatusCallBack;

    //消息到达回调
    private MessageCallBack messageCallBack;

    private TimeoutMessageCallBack timeoutMessageCallBack;

    /**
     * 是否已经连接channel
     */
    private volatile boolean isConnected =false;

    /**
     * 只初始化一次
     */
    private boolean isInit=false;

    public TCPClient() {
        preInit();
    }
    /**
     * 这些初始化先于使用者的设置，使用者设置有可能覆盖
     */
    private void preInit() {
        initQos();
        initProtocol();
        initProcessorManage();
        initEventProcessor();
        initTcp();
        initMessageProcessor();
        initCallBack();
    }

    /**
     * 依赖注入
     */
    private void DI(){
        //客户端默认使用id生成seq
        VitalMessageFactory.setSeqStrategy(new IDSeqStrategy());
        this.clientReceiveQos.setSendQos(clientSendQos);
        this.clientSendQos
                .setTimeoutMessageCallBack(timeoutMessageCallBack)
                .setSender(sender);

        if(protocol instanceof VitalProtocol){
            VitalProtocol vitalProtocol= (VitalProtocol) protocol;

            ClientDefaultCommandHandler clientDefaultCommandHandler = new ClientDefaultCommandHandler();
            VitalSendHelper vitalSendHelper = new VitalSendHelper();

            vitalProtocol
                    .setDecode(new ProtobufDecoder(VitalPB.Frame.getDefaultInstance()))
                    .setEncode(new ProtobufEncoder())
                    .setVitalSendHelper(vitalSendHelper)
                    .setCommandHandler(clientDefaultCommandHandler);
            clientDefaultCommandHandler
                    .setClientProcessorManager(clientProcessorManager)
                    .setClientReceiveQos(clientReceiveQos)
                    .setClientSendQos(clientSendQos)
                    .setClientUserProcessorManager(clientUserProcessorManager)
                    .setProtocol(protocol)
                    .setMessageCallBack(messageCallBack);


            vitalSendHelper
                    .setSendQos(clientSendQos);

        }

        tcpConnect
                .setProtocolClass(protocolClass)
                .setChannelStatusCallBack(channelStatusCallBack)
                .setClientConnectionEventListener(clientConnectionEventListener)
                .setProtocolManager(protocolManager);

        if(sender instanceof VitalSender){
            VitalSender vitalSender = (VitalSender) sender;
            vitalSender
                    .setTcpConnect(tcpConnect)
                    .setProtocol(protocol);
        }
        heartBeatLauncher
                .setTcpConnect(tcpConnect)
                .setProtocol(protocol);

        authSuccessProcessor.setClientSendQos(clientSendQos);

        exceptionProcessor.setClientSendQos(clientSendQos);

        kickoutProcessor.setTcpClient(this);

        this.clientProcessorManager.registerProcessor(VitalPB.MessageType.AuthSuccessMessageType.name(), authSuccessProcessor);
        this.clientProcessorManager.registerProcessor(VitalPB.MessageType.ExceptionMessageType.name(), exceptionProcessor);
        this.clientProcessorManager.registerProcessor(VitalPB.MessageType.KickoutMessageType.name(), kickoutProcessor);

    }
    /**
     * 初始化qos
     */
    private void initQos() {
        this.clientReceiveQos = new ClientReceiveQos();
        this.clientSendQos = new ClientSendQos();
        this.qosExecutor = new ThreadPoolExecutor(1,
                1,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new DefaultThreadFactory("vital-client-qos-executor", true),
                new ThreadPoolExecutor.DiscardPolicy());
    }

    /**
     * 初始化协议
     */
    private void initProtocol() {
        this.protocolManager = new ProtocolManager();
        this.protocolClass = VitalProtocol.class;
        this.protocol = new VitalProtocol();

        protocolManager.registerProtocol(protocolClass.getSimpleName(), protocol);
    }

    private void initProcessorManage() {
        this.clientProcessorManager = new ClientProcessorManager();
        this.clientUserProcessorManager = new ClientProcessorManager();


    }


    /**
     * 初始化EventProcessor，监听事件处理器
     */
    private void initEventProcessor() {

        this.clientConnectionEventListener = new ClientConnectionEventListener();

        clientConnectionEventListener.addConnectionEventProcessor(ConnectionEventType.CONNECT, new ClientConnectEventProcessor());
        clientConnectionEventListener.addConnectionEventProcessor(ConnectionEventType.CLOSE, new ClientDisconnectEventProcessor());
    }

    /**
     * 初始化tcp相关功能
     */
    private void initTcp() {
        this.tcpConnect = new TCPConnect();
        this.sender=new VitalSender();
        this.heartBeatLauncher=new HeartBeatLauncher();
    }

    private void initMessageProcessor(){
        this.authSuccessProcessor = new AuthSuccessProcessor();
        this.exceptionProcessor = new ExceptionProcessor();
        this.kickoutProcessor = new KickoutProcessor();



    }

    /**
     * 初始化回调
     */
    private void initCallBack(){
        this.channelStatusCallBack = new ChannelStatusCallBack() {
            @Override
            public void onOpen(Channel channel) {
                tcpConnect.setChannel(channel);
                isConnected =true;
            }

            @Override
            public void onClose(Channel channel) {
                isConnected =false;
                boolean flag=false;
                if(tcpConnect.isToConnect()){
                    //重连的条件是当前关闭的channel中Connection的id和配置中的id相等
                    Attribute<Connection> attr = channel.attr(Connection.CONNECTION);
                    if(attr!=null){
                        Connection connection = attr.get();
                        if(connection!=null){
                            if (connection.getId().equals(ClientVitalGenericOption.ID.value())) {
                                flag=true;
                            }
                        }
                    }
                    if(flag){
                        // todo 重连回调
                        tcpConnect.start();
                        sendAuth(new RequestSendCallBack() {
                            @Override
                            public void onResponse(MessageWrapper response) {
                                log.info("重连成功");
                            }

                            @Override
                            public void onAck(MessageWrapper messageWrapper) {

                            }

                            @Override
                            public void onException(MessageWrapper exception) {

                            }
                        });
                    }else {
                    }

                }else {
                    tcpConnect.shutdown();
                }

            }
        };

        this.timeoutMessageCallBack=new TimeoutMessageCallBack<VitalMessageWrapper>() {
            @Override
            public void onTimeout(List<VitalMessageWrapper> timeoutMessages) {
                for (VitalMessageWrapper vitalMessageWrapper : timeoutMessages) {
                    VitalPB.Frame exception = (VitalPB.Frame) protocol.createException(vitalMessageWrapper.getSeq(), ErrorCode.SEND_FAILED.getExtra(), ErrorCode.SEND_FAILED.getCode());
                    clientSendQos.invokeExceptionCallback(new VitalMessageWrapper(exception));
                }
            }
        };

    }
    /**
     * 发送认证消息
     */
    public void sendAuth(RequestSendCallBack requestSendCallBack) {
        while (!isConnected){

        }
        VitalPB.Frame auth = (VitalPB.Frame) protocol.createAuthRequest(ClientVitalGenericOption.ID.value(), ClientVitalGenericOption.TOKEN.value());
        if(requestSendCallBack==null){
            requestSendCallBack=new RequestSendCallBack() {
                @Override
                public void onResponse(MessageWrapper response) {

                }

                @Override
                public void onAck(MessageWrapper messageWrapper) {

                }

                @Override
                public void onException(MessageWrapper exception) {

                }
            };
        }
        sender.send(auth, requestSendCallBack);
    }

    public void sendDisAuth(SendCallBack sendCallBack){
        VitalPB.Frame disAuth = (VitalPB.Frame) protocol.createDisAuth();

        sender.send(disAuth, new SendCallBack<MessageWrapper>() {
            @Override
            public void onAck(MessageWrapper messageWrapper) {
                clear();
                log.info("注销成功");
                if(sendCallBack !=null){
                    sendCallBack.onAck(messageWrapper);
                }
            }

            @Override
            public void onException(MessageWrapper exception) {
                if(sendCallBack !=null){
                    sendCallBack.onException(exception);
                }
            }

        });
    }

    public void clear(){
        tcpConnect.setToConnect(false);
        tcpConnect.setChannel(null);
        clientSendQos.clear();
        clientReceiveQos.clear();
    }

    /**
     * 调用start后调用
     */
    private void afterInit() {
        DI();
    }



    /**
     * 启动TCPClient
     */
    public void start() {
         if(!isInit){
             afterInit();
             qosWithHeartBeatStart();
             isInit=true;
         }
        if(tcpConnect.getChannel()!=null){
            log.error("已经连接服务器，必须先注销，或者注销未完成");
            throw new RuntimeException("已经连接服务器，必须先注销，或者注销未完成");
        }else {
            tcpConnect.setToConnect(true);
            tcpConnect.start();
        }

    }

    private void qosWithHeartBeatStart() {
        qosExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long sendQosLastRun = 0;
                long receiveQosLastRun = 0;
                long heartBeatLastRun = System.currentTimeMillis();
                while (true) {
                    if (System.currentTimeMillis() - sendQosLastRun >= ClientVitalGenericOption.SEND_QOS_INTERVAL_TIME.value()) {
                        sendQosLastRun = System.currentTimeMillis();
                        clientSendQos.checkTask();
                    }
                    if (System.currentTimeMillis() - receiveQosLastRun >= ClientVitalGenericOption.RECEIVE_QOS_INTERVAL_TIME.value()) {
                        receiveQosLastRun = System.currentTimeMillis();
                        clientReceiveQos.checkTask();
                    }
                    if (System.currentTimeMillis() - heartBeatLastRun >= ClientVitalGenericOption.HEART_BEAT_INTERVAL_TIME.value()) {
                        heartBeatLastRun = System.currentTimeMillis();
                        heartBeatLauncher.heartBeatTask();
                    }
                }
            }
        });
    }

    private void qosWithHeartBeatShutdown() {
        if (qosExecutor != null) {
            qosExecutor.shutdownNow();
        }
        qosExecutor = null;
    }


    /**
     * 关闭TCPClient
     */
    public void shutdown() {
        qosWithHeartBeatShutdown();
        tcpConnect.shutdown();
    }

    /**
     * 注册protocol
     *
     * @param name     协议类的class的simpleName
     * @param protocol
     */
    public TCPClient registerProtocol(String name, Protocol protocol) {
        protocolManager.registerProtocol(name, protocol);
        return this;
    }

    /**
     * 设置protocol类的class，框架将使用该协议，会覆盖默认设置，使用者的设置优先
     *
     * @param protocolClass
     * @return
     */
    public TCPClient setProtocolClass(Class<? extends Protocol> protocolClass) {
        this.protocolClass = protocolClass;
        return this;
    }

    /**
     * 消息到达，通知回调，虽然可以通过设置processor处理不同类型的消息，但是如果需要处理所有类型消息，就需要都设置processor，相对比较麻烦，所有提供了该回调
     *
     * @param messageCallBack
     */
    public TCPClient setMessageCallBack(MessageCallBack messageCallBack) {
        this.messageCallBack = messageCallBack;
        return this;
    }

    /**
     * 会覆盖默认设置，使用者的设置优先,command为key，只会存在一个对应value
     *
     * @param command
     * @param processor
     * @return
     */
    public TCPClient registerProcessor(String command, Processor processor) {
        clientProcessorManager.registerProcessor(command, processor);
        return this;
    }

    /**
     *
     *
     * @param command
     * @param processor
     * @return
     */
    public TCPClient registerUserProcessor(String command, Processor processor) {
        clientUserProcessorManager.registerProcessor(command, processor);
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






    public Sender getSender() {
        return sender;
    }

    public Protocol getProtocol() {
        return protocol;
    }
}
