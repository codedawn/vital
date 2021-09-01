package com.codedawn.vital.client;

import com.codedawn.vital.client.command.ClientDefaultCommandHandler;
import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.client.connector.Sender;
import com.codedawn.vital.client.connector.TCPConnect;
import com.codedawn.vital.client.connector.VitalSender;
import com.codedawn.vital.client.factory.ClientVitalMessageFactory;
import com.codedawn.vital.client.processor.ClientProcessorManager;
import com.codedawn.vital.client.processor.impl.client.AuthSuccessProcessor;
import com.codedawn.vital.client.processor.impl.client.DisAuthFinishProcessor;
import com.codedawn.vital.client.processor.impl.client.ExceptionProcessor;
import com.codedawn.vital.client.qos.ClientReceiveQos;
import com.codedawn.vital.client.qos.ClientSendQos;
import com.codedawn.vital.client.qos.HeartBeatLauncher;
import com.codedawn.vital.client.session.ClientConnectionEventListener;
import com.codedawn.vital.client.session.impl.ClientConnectEventProcessor;
import com.codedawn.vital.client.session.impl.ClientDisconnectEventProcessor;
import com.codedawn.vital.server.callback.*;
import com.codedawn.vital.server.config.VitalOption;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.*;
import com.codedawn.vital.server.session.ConnectionEventType;
import io.netty.channel.Channel;
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

    private Class<? extends Protocol> protocolClass= null;

    private ProtocolManager protocolManager=new ProtocolManager();

    private TCPConnect tcpConnect ;

    private ExecutorService qosExecutor;

    private ClientReceiveQos clientReceiveQos = new ClientReceiveQos();

    private ClientSendQos clientSendQos = new ClientSendQos();


    private ClientConnectionEventListener clientConnectionEventListener = new ClientConnectionEventListener();

    private ClientProcessorManager clientProcessorManager = new ClientProcessorManager();

    public Sender sender=new VitalSender(clientSendQos);

    private HeartBeatLauncher heartBeatLauncher=new HeartBeatLauncher();

    private AuthSuccessProcessor authSuccessProcessor = null;

    private ExceptionProcessor exceptionProcessor = null;

    private DisAuthFinishProcessor disAuthFinishProcessor=null;

    private AuthResponseCallBack authResponseCallBack;

    private ChannelStatusCallBack channelStatusCallBack;

    //消息到达回调
    private MessageCallBack messageCallBack = null;



    public TCPClient() {
        preInit();
    }

    /**
     * 发送认证消息
     */
    public void auth() {
        VitalProtobuf.Protocol auth = ClientVitalMessageFactory.createAuth(ClientVitalGenericOption.ID.value(), ClientVitalGenericOption.TOKEN.value());
        sender.send(auth, new ResponseCallBack<VitalMessageWrapper>() {
            @Override
            public void onAck(VitalMessageWrapper messageWrapper) {
                log.info("认证消息送达");
            }

            @Override
            public void exception(VitalMessageWrapper messageWrapper) {
                log.info("认证消息发生错误：{}",messageWrapper.getProtocol().getExceptionMessage().getExtra());
                if (authResponseCallBack != null) {
                    authResponseCallBack.exception(messageWrapper);
                }
            }

        });
    }
    /**
     * 这些初始化先于使用者的设置，使用者设置有可能覆盖
     */
    private void preInit() {

        clientSendQos.setSender(sender);

        channelStatusCallBack=new ChannelStatusCallBack() {
            @Override
            public void open(Channel channel) {
                sender.setChannel(channel);
                auth();
            }

            @Override
            public void close(Channel channel) {
                sender.setChannel(null);
                tcpConnect.shutdown();
                if (tcpConnect.isConnect()) {
                    tcpConnect.start();
                }
            }
        };
        clientSendQos.setTimeoutMessageCallBack(new TimeoutMessageCallBack<VitalMessageWrapper>() {
            @Override
            public void timeout(List<VitalMessageWrapper> timeoutMessages) {
                for (VitalMessageWrapper vitalMessageWrapper : timeoutMessages) {
                    VitalProtobuf.Protocol exception = ClientVitalMessageFactory.createException(vitalMessageWrapper.getSeq(), ErrorCode.SEND_FAILED.getExtra(),ErrorCode.SEND_FAILED.getCode());
                    sender.invokeExceptionCallback(new VitalMessageWrapper(exception));
                }
            }
        });
    }

    /**
     * 使用者没有进行初始化使用默认设置
     */
    private void afterInit() {
        qosExecutor=new ThreadPoolExecutor(1,
                1,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new DefaultThreadFactory("vital-client-qos-executor", true),
                new ThreadPoolExecutor.DiscardPolicy());

        if (protocolClass == null) {
            protocolClass = VitalTCPProtocol.class;
            protocolManager.registerProtocol(protocolClass.getSimpleName(),new VitalTCPProtocol(new ClientDefaultCommandHandler(clientProcessorManager, clientReceiveQos, clientSendQos, sender,messageCallBack)));
        }

        tcpConnect = new TCPConnect(protocolClass, protocolManager, clientConnectionEventListener,channelStatusCallBack);


        heartBeatLauncher.setTcpConnect(tcpConnect);


        if (authSuccessProcessor == null) {
            authSuccessProcessor = new AuthSuccessProcessor().setAuthResponseCallBack(authResponseCallBack);
        }
        else {
            authSuccessProcessor.setAuthResponseCallBack(authResponseCallBack);
        }


        if (exceptionProcessor == null) {
            exceptionProcessor = new ExceptionProcessor().setSender(sender);
        }else {
            exceptionProcessor.setSender(sender);
        }

        if (disAuthFinishProcessor == null) {
            disAuthFinishProcessor = new DisAuthFinishProcessor().setTcpConnect(tcpConnect);
        }else {
            disAuthFinishProcessor.setTcpConnect(tcpConnect);
        }


        clientProcessorManager.registerProcessor(VitalProtobuf.MessageType.AuthSuccessMessageType.toString(),authSuccessProcessor);
        clientProcessorManager.registerProcessor(VitalProtobuf.MessageType.ExceptionMessageType.toString(),exceptionProcessor);
        clientProcessorManager.registerProcessor(VitalProtobuf.MessageType.DisAuthFinishMessageType.toString(),disAuthFinishProcessor);

        clientConnectionEventListener.addConnectionEventProcessor(ConnectionEventType.CONNECT,new ClientConnectEventProcessor());
        clientConnectionEventListener.addConnectionEventProcessor(ConnectionEventType.CLOSE,new ClientDisconnectEventProcessor(tcpConnect));
    }

    /**
     * 修改框架参数配置{@link ClientVitalGenericOption}
     * @param option
     * @param value
     * @param <T>
     * @return
     */
    public  <T> TCPClient option(VitalOption<T> option, T value){
        ClientVitalGenericOption.option(option, value);
        return this;
    }

    /**
     * 启动TCPClient
     */
    public void start() {
        afterInit();
        qosWithHeartBeatStart();
        tcpConnect.start();
    }

    private void qosWithHeartBeatStart() {
       qosExecutor.execute(new Runnable() {
           @Override
           public void run() {
               long sendQosLastRun = 0;
               long receiveQosLastRun=0;
               long heartBeatLastRun = System.currentTimeMillis();
               while (true) {
                   if (System.currentTimeMillis()-sendQosLastRun >= ClientVitalGenericOption.SEND_QOS_INTERVAL_TIME.value()) {
                       sendQosLastRun=System.currentTimeMillis();
                       clientSendQos.checkTask();
                   }
                   if (System.currentTimeMillis()-receiveQosLastRun >= ClientVitalGenericOption.RECEIVE_QOS_INTERVAL_TIME.value()) {
                       receiveQosLastRun=System.currentTimeMillis();
                       clientReceiveQos.checkTask();
                   }
                   if (System.currentTimeMillis()-heartBeatLastRun >= ClientVitalGenericOption.HEART_BEAT_INTERVAL_TIME.value()) {
                       heartBeatLastRun=System.currentTimeMillis();
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
        clientProcessorManager.registerProcessor(command, processor);
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

    public TCPClient setAuthResponseCallBack(AuthResponseCallBack authResponseCallBack) {
        this.authResponseCallBack = authResponseCallBack;
        return this;
    }

    public  Sender getSender() {
        return sender;
    }

}
