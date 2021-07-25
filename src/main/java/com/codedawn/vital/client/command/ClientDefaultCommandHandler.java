package com.codedawn.vital.client.command;

import com.codedawn.vital.client.TCPClient;
import com.codedawn.vital.client.callback.ClientMessageCallBack;
import com.codedawn.vital.client.context.ClientMessageContext;
import com.codedawn.vital.command.CommandHandler;
import com.codedawn.vital.context.MessageContext;
import com.codedawn.vital.factory.VitalMessageFactory;
import com.codedawn.vital.proto.MessageWrapper;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.qos.ReceiveQos;
import com.codedawn.vital.qos.SendQos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-26 22:02
 */
public class ClientDefaultCommandHandler implements CommandHandler {

    private static Logger log = LoggerFactory.getLogger(ClientDefaultCommandHandler.class);

    private ReceiveQos receiveQos;

    private SendQos sendQos;

    private ClientMessageCallBack<VitalMessageWrapper> clientMessageCallBack;


    public ClientDefaultCommandHandler(ReceiveQos receiveQos, SendQos sendQos, ClientMessageCallBack clientMessageCallBack) {
        this.receiveQos = receiveQos;
        this.sendQos = sendQos;
        this.clientMessageCallBack = clientMessageCallBack;
    }

    @Override
    public void handle(MessageContext messageContext, Object msg) {

        //todo 接受消息逻辑需要调整
        ClientMessageContext clientMessageContext = (ClientMessageContext) messageContext;
        VitalProtobuf.Protocol message = (VitalProtobuf.Protocol) msg;

        //已经接受过的消息，不放行，
        MessageWrapper messageWrapper = checkMsgWhetherDuplication(message);
        if (messageWrapper!=null) {
            //重复接收到的消息也需要发送ack，用第一个版本
            this.ackMsg(clientMessageContext,messageWrapper);
            return;
        }else {
            //不是重复接收的消息发送ack
            messageWrapper = new VitalMessageWrapper(message);
            this.ackMsg(clientMessageContext,messageWrapper);
        }
        //如果是没有重复接收的ack，也就是第一次到达的ack
        if (ifAck(messageWrapper)) {
            //是ackMessage，可以return了
            return;
        }
        //接下来就是用户进行接收的消息
        notifyToReceive(messageWrapper);


    }

    private void notifyToReceive(MessageWrapper messageWrapper) {
            clientMessageCallBack.messageArrive((VitalMessageWrapper) messageWrapper);
    }
    /**
     * 检查是否是ack消息，如果是需要回调，还有移除{@link SendQos}中的对应消息，因为对方已经收到
     * @param messageWrapper
     * @return
     */
    private boolean ifAck(MessageWrapper messageWrapper) {
        VitalProtobuf.Protocol message = (VitalProtobuf.Protocol) messageWrapper.getMessage();
        VitalProtobuf.DataType dataType = message.getDataType();
        if (dataType == VitalProtobuf.DataType.AckMessageType) {
            VitalProtobuf.AckMessage ackMessage = message.getAckMessage();
            //callback
            VitalMessageWrapper vitalMessageWrapper = new VitalMessageWrapper(message);
            TCPClient.sender.invokeCallback(vitalMessageWrapper);

            //qos ,移除ack对应的发送的消息，并且添加ack到接受消息队列
            receiveQos.addIfAbsent(vitalMessageWrapper.getQosId(),vitalMessageWrapper);
            sendQos.remove(vitalMessageWrapper.getAckQosId());
            return true;
        } else if (dataType == VitalProtobuf.DataType.AckMessageWithExtraType) {
            VitalProtobuf.AckMessageWithExtra ackMessageWithExtra = message.getAckMessageWithExtra();
            //callback
            VitalMessageWrapper vitalMessageWrapper = new VitalMessageWrapper(message, ackMessageWithExtra.getAckPerId(), ackMessageWithExtra.getAckTimeStamp());
            TCPClient.sender.invokeCallback(vitalMessageWrapper);

            //qos ,移除ack对应的发送的消息，并且添加ack到接受消息队列
            receiveQos.addIfAbsent(vitalMessageWrapper.getQosId(),vitalMessageWrapper);
            sendQos.remove(vitalMessageWrapper.getAckQosId());
            return true;
        }

        return false;
    }

    private MessageWrapper checkMsgWhetherDuplication(VitalProtobuf.Protocol message ) {
        log.info("客户端收到消息：{}",message.toString());
        String qosId = message.getQosId();
        MessageWrapper messageWrapper = receiveQos.getIfHad(qosId);
        if (messageWrapper != null) {
            return messageWrapper;

        }
        return null;
    }

    private boolean checkWhetherAck(VitalProtobuf.Protocol message) {
        VitalProtobuf.DataType dataType = message.getDataType();
        if (dataType == VitalProtobuf.DataType.AckMessageType) {
            return true;
        } else if (dataType == VitalProtobuf.DataType.AckMessageWithExtraType) {
            return true;
        }
        return false;
    }

    private void ackMsg(ClientMessageContext clientMessageContext, MessageWrapper messageWrapper) {
        //不需要qos,或者是ack，ack不需要再ack，禁止套娃
        if(!messageWrapper.getQos()||checkWhetherAck((VitalProtobuf.Protocol) messageWrapper.getMessage())){
            return;
        }
        //加入ReceiveQos，防止qos导致消息重复
        receiveQos.addIfAbsent(messageWrapper.getQosId(),messageWrapper);
        VitalProtobuf.Protocol ack = null;
        //ack是否携带id和时间戳
        if(!messageWrapper.getAckExtra()) {
            ack= VitalMessageFactory.createAck((VitalProtobuf.Protocol) messageWrapper.getMessage());
        }else {
            //todo ackExtra
            log.info("客户端不对ackExtra做出特殊响应，也就是说和普通ack一样，所以不要尝试去使用ackPerID和ackTimestamp");
            ack = VitalMessageFactory.createAckWithExtra((VitalProtobuf.Protocol) messageWrapper.getMessage(),"", 0);
        }
        clientMessageContext.getChannelHandlerContext().channel().writeAndFlush(ack);
    }


}
