package com.codedawn.vital.client.command;

import com.codedawn.vital.client.processor.ClientProcessorManager;
import com.codedawn.vital.client.qos.ClientReceiveQos;
import com.codedawn.vital.client.qos.ClientSendQos;
import com.codedawn.vital.server.callback.MessageCallBack;
import com.codedawn.vital.server.command.ServerDefaultCommandHandler;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;

/**
 * @author codedawn
 * @date 2021-07-24 22:30
 */
public class ClientDefaultCommandHandler extends ServerDefaultCommandHandler {

//    private static Logger log = LoggerFactory.getLogger(ClientDefaultCommandHandler.class);
//
//    protected ClientProcessorManager clientProcessorManager;
//
//
//    protected ClientReceiveQos clientReceiveQos;
//
//    protected ClientSendQos clientSendQos;
//
//    protected ClientProcessorManager clientUserProcessorManager;
//
//    protected Protocol<VitalPB.Frame> protocol;
//
//    protected MessageCallBack messageCallBack;



    public ClientDefaultCommandHandler() {
    }

//    public ClientDefaultCommandHandler(ClientProcessorManager clientProcessorManager, ClientReceiveQos clientReceiveQos, ClientSendQos clientSendQos, ClientProcessorManager clientUserProcessorManager, MessageCallBack messageCallBack) {
//        this.clientProcessorManager = clientProcessorManager;
//        this.clientReceiveQos = clientReceiveQos;
//        this.clientSendQos = clientSendQos;
//        this.clientUserProcessorManager = clientUserProcessorManager;
//        this.messageCallBack = messageCallBack;
//    }


    @Override
    protected MessageWrapper getMessageWrapper(VitalPB.Frame  message) {
        //不需要qos或者不需要ackExtra，设置ackTimestamp也没有意义

        //客户端不应该响应ackExtra
        return new VitalMessageWrapper(message);
    }


    public ClientDefaultCommandHandler setClientProcessorManager(ClientProcessorManager clientProcessorManager) {
//        this.clientProcessorManager = clientProcessorManager;
        super.processorManager=clientProcessorManager;
        return this;
    }

    public ClientDefaultCommandHandler setClientReceiveQos(ClientReceiveQos clientReceiveQos) {
//        this.clientReceiveQos = clientReceiveQos;
        super.receiveQos = clientReceiveQos;
        return this;
    }

    public ClientDefaultCommandHandler setClientSendQos(ClientSendQos clientSendQos) {
//        this.clientSendQos = clientSendQos;
        super.sendQos = clientSendQos;
        return this;
    }

    public ClientDefaultCommandHandler setClientUserProcessorManager(ClientProcessorManager clientUserProcessorManager) {
//        this.clientUserProcessorManager = clientUserProcessorManager;
        this.userProcessorManager = clientUserProcessorManager;
        return this;
    }

    public ClientDefaultCommandHandler setProtocol(Protocol<VitalPB.Frame> protocol) {
//        this.protocol = protocol;
        super.protocol = protocol;
        return this;
    }

    public ClientDefaultCommandHandler setMessageCallBack(MessageCallBack messageCallBack) {
//        this.messageCallBack = messageCallBack;
        super.messageCallBack = messageCallBack;
        return this;
    }
}
