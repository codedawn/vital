package com.codedawn.vital.command;

import com.codedawn.vital.callback.MessageCallBack;
import com.codedawn.vital.callback.ResponseCallBack;
import com.codedawn.vital.connector.Sender;
import com.codedawn.vital.processor.ProcessorManager;
import com.codedawn.vital.proto.MessageWrapper;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.qos.ReceiveQos;
import com.codedawn.vital.qos.SendQos;

/**
 * @author codedawn
 * @date 2021-07-29 9:03
 */
public class ClientDefaultCommandHandler extends ServerDefaultCommandHandler{

    private Sender sender;


    public ClientDefaultCommandHandler(ProcessorManager processorManager, ReceiveQos receiveQos, SendQos sendQos, ResponseCallBack responseCallBack, Sender sender, MessageCallBack messageCallBack) {
        super(processorManager,null, receiveQos, sendQos, responseCallBack,messageCallBack);
        super.serverSide = false;
        this.sender = sender;
    }

    @Override
    protected void callBack(VitalMessageWrapper vitalMessageWrapper) {
        sender.invokeCallback(vitalMessageWrapper);
    }

    @Override
    protected MessageWrapper getMessageWrapper(VitalProtobuf.Protocol message) {
        //不需要qos，设置ackTimestamp也没有意义
        if(!message.getQos()){
            return new VitalMessageWrapper(message);
        }
        return  new VitalMessageWrapper(message, "",0L );
    }
}
