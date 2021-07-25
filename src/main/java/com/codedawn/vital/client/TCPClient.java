package com.codedawn.vital.client;

import com.codedawn.vital.client.callback.ClientMessageCallBack;
import com.codedawn.vital.client.command.ClientDefaultCommandHandler;
import com.codedawn.vital.client.connector.Sender;
import com.codedawn.vital.client.connector.TCPConnect;
import com.codedawn.vital.client.connector.VitalSender;
import com.codedawn.vital.client.proto.VitalClientProtocol;
import com.codedawn.vital.proto.Protocol;
import com.codedawn.vital.proto.ProtocolManager;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.qos.ReceiveQos;
import com.codedawn.vital.qos.SendQos;

/**
 * @author codedawn
 * @date 2021-07-26 22:17
 */
public class TCPClient {

    private Class<? extends Protocol> protocolClass= VitalClientProtocol.class;

    private ProtocolManager protocolManager=new ProtocolManager();

    private TCPConnect tcpConnect ;

    private ReceiveQos receiveQos = new ReceiveQos();

    private SendQos sendQos = new SendQos();

    public static Sender sender;

    public ClientMessageCallBack clientMessageCallBack;

    public TCPClient() {
        init();
    }

    private void init() {
        clientMessageCallBack = new ClientMessageCallBack<VitalMessageWrapper>() {
            @Override
            public void messageArrive(VitalMessageWrapper messageWrapper) {
                System.out.println(messageWrapper.toString());
            }
        };
        protocolManager.registerProtocol(protocolClass.getSimpleName(),new VitalClientProtocol(new ClientDefaultCommandHandler(receiveQos,sendQos, clientMessageCallBack)));
        tcpConnect = new TCPConnect(protocolClass, protocolManager);
        sender = new VitalSender(tcpConnect,sendQos);
    }

    public void start() {
        receiveQos.start();
        sendQos.start();
        tcpConnect.start();
    }

}
