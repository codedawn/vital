package com.codedawn.vital.server;

import com.codedawn.vital.command.CommandHandler;
import com.codedawn.vital.proto.*;
import com.codedawn.vital.server.callback.ServerMessageCallBack;
import com.codedawn.vital.server.command.ServerDefaultCommandHandler;
import com.codedawn.vital.server.connector.TCPConnector;
import com.codedawn.vital.server.processor.ProcessorManager;
import com.codedawn.vital.server.processor.impl.AuthServerProcessor;
import com.codedawn.vital.server.proto.VitalServerProtocol;
import com.codedawn.vital.qos.ReceiveQos;
import com.codedawn.vital.qos.SendQos;
import com.codedawn.vital.server.session.ConnectionManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author codedawn
 * @date 2021-07-21 9:38
 */
public class TCPServer {

    private static Logger log = LoggerFactory.getLogger(TCPServer.class);


    private ReceiveQos receiveQos=new ReceiveQos();

    private SendQos sendQos = new SendQos();

    private ProtocolManager protocolManager=new ProtocolManager();

    private Class<? extends Protocol> protocolClass= VitalServerProtocol.class;

    private TCPConnector tcpConnector=new TCPConnector(protocolClass,protocolManager);


    private ProcessorManager processorManager=new ProcessorManager();


    private ConnectionManage connectionManage=new ConnectionManage();

    public TCPServer() {
        init();
    }

    public void init() {
        CommandHandler serverDefaultCommandHandler = new ServerDefaultCommandHandler(processorManager, receiveQos, sendQos, new ServerMessageCallBack<VitalMessageWrapper>() {
            @Override
            public void ackArrived(VitalMessageWrapper messageWrapper) {
                System.out.println(messageWrapper.toString());
            }


        });
        processorManager.registerProcessor(VitalProtobuf.DataType.AuthMessageType.toString(),new AuthServerProcessor(connectionManage));

        protocolManager.registerProtocol(protocolClass.getSimpleName(),new VitalServerProtocol(serverDefaultCommandHandler));
    }

    public void start() {
        receiveQos.start();
        sendQos.start();
        tcpConnector.start();
    }

}
