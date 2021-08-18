package com.codedawn.vital.client.command;

import com.codedawn.vital.client.connector.Sender;
import com.codedawn.vital.client.connector.VitalClientSendHelper;
import com.codedawn.vital.client.context.DefaultMessageContext;
import com.codedawn.vital.client.factory.ClientVitalMessageFactory;
import com.codedawn.vital.client.processor.ClientProcessorManager;
import com.codedawn.vital.client.qos.ClientReceiveQos;
import com.codedawn.vital.client.qos.ClientSendQos;
import com.codedawn.vital.server.callback.MessageCallBack;
import com.codedawn.vital.server.command.CommandHandler;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalProtobuf;
import com.codedawn.vital.server.util.AddressUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-24 22:30
 */
public class ClientDefaultCommandHandler implements CommandHandler<DefaultMessageContext> {

    private static Logger log = LoggerFactory.getLogger(ClientDefaultCommandHandler.class);

    private ClientProcessorManager clientProcessorManager;


    private ClientReceiveQos clientReceiveQos;

    private ClientSendQos clientSendQos;

    private Sender sender;


    private MessageCallBack messageCallBack;


    public ClientDefaultCommandHandler(ClientProcessorManager clientProcessorManager, ClientReceiveQos clientReceiveQos, ClientSendQos clientSendQos, Sender sender, MessageCallBack messageCallBack) {
        this.clientProcessorManager = clientProcessorManager;
        this.clientReceiveQos = clientReceiveQos;
        this.clientSendQos = clientSendQos;
        this.sender = sender;
        this.messageCallBack = messageCallBack;
    }

    public ClientProcessorManager getProcessorManager() {
        return clientProcessorManager;
    }

    public ClientDefaultCommandHandler setProcessorManager(ClientProcessorManager clientProcessorManager) {
        this.clientProcessorManager = clientProcessorManager;
        return this;
    }



    public ClientReceiveQos getReceiveQos() {
        return clientReceiveQos;
    }

    public ClientDefaultCommandHandler setReceiveQos(ClientReceiveQos clientReceiveQos) {
        this.clientReceiveQos = clientReceiveQos;
        return this;
    }

    public ClientSendQos getSendQos() {
        return clientSendQos;
    }

    public ClientDefaultCommandHandler setSendQos(ClientSendQos clientSendQos) {
        this.clientSendQos = clientSendQos;
        return this;
    }



    public MessageCallBack getMessageCallBack() {
        return messageCallBack;
    }

    public ClientDefaultCommandHandler setMessageCallBack(MessageCallBack messageCallBack) {
        this.messageCallBack = messageCallBack;
        return this;
    }

    @Override
    public void handle(com.codedawn.vital.client.context.DefaultMessageContext messageContext, Object msg) {
        VitalProtobuf.Protocol message = (VitalProtobuf.Protocol) msg;
        //如果是心跳包，直接过滤
        if (checkWhetherHeartBeat(message)) {
            log.info("来自{}的心跳", AddressUtil.parseRemoteAddress(messageContext.getChannelHandlerContext().channel()));
            return;
        }
        MessageWrapper messageWrapper = checkMsgWhetherDuplication(message);
        boolean dupli = false;
        //已经接受过的消息，
        if (messageWrapper!=null) {
            dupli = true;
            log.info("接收到重复消息：{}",messageWrapper.toString());
        }else{
            //不是重复接收的消息
            messageWrapper = getMessageWrapper(message);
        }
        //如果是ack，不管有没有重复，都需要进去，不过重复消息不会再次回调
        if (ifAck(messageWrapper,dupli)) {
            //是ackMessage，可以return了
            return;
        }
        //下面已经不会是ack了
        //重复接收的消息也需要ack，因为收到重复消息，说明另一方没有收到ack（或者延迟了），注意：到这已经不可能是ack消息了
        //todo 默认雪花算法实现生成id，后续拓展可以自定义
        this.ackMsg(messageContext,messageWrapper,dupli);
        //到这里重复消息应该被拦截了，下面是处理，应该是不重复消息才能进行
        if (dupli) {
            return;
        }

        //接下来就是进行转发的消息
        notifyToReceive(messageWrapper);
        this.process(messageContext,messageWrapper);
    }

    private boolean checkWhetherHeartBeat(VitalProtobuf.Protocol  message) {
        if (message.getDataType()==VitalProtobuf.DataType.HeartbeatType) {
            return true;
        }
        return false;
    }

    /**
     * 消息到达，通知回调，虽然可以通过设置processor处理不同类型的消息，但是如果需要处理所有类型消息，就需要都设置processor，相对比较麻烦，所有提供了该回调
     * @param messageWrapper
     */
    private void notifyToReceive(MessageWrapper messageWrapper) {
        if (messageCallBack != null) {
            messageCallBack.onMessage(messageWrapper);
        }
    }
    protected MessageWrapper getMessageWrapper(VitalProtobuf.Protocol message) {
        //不需要qos，设置ackTimestamp也没有意义
        if(!message.getQos()){
            return new VitalMessageWrapper(message);
        }
        return  new VitalMessageWrapper(message, "",0L );
    }

    /**
     * 分发消息到processor
     * @param defaultMessageContext
     * @param messageWrapper
     */
    private void process(final com.codedawn.vital.client.context.DefaultMessageContext defaultMessageContext, final MessageWrapper messageWrapper) {


        VitalMessageWrapper vitalProtocolWrapper = (VitalMessageWrapper) messageWrapper;
        VitalProtobuf.Protocol p =  vitalProtocolWrapper.getMessage();
        String dataTypeStr = p.getDataType().toString();

        if (clientProcessorManager != null) {
            //派发到指定的Processor
            Processor processor = clientProcessorManager.getProcessor(dataTypeStr);
            if (processor != null) {
                processor.process(defaultMessageContext, messageWrapper);
            }

        }

    }
    /**
     * 检查是否是ack消息，如果是需要回调，还有移除{@link ClientSendQos}中的对应消息，因为对方已经收到
     * 如果是重复接收的ack，不会再次回调
     * @param messageWrapper
     * @param dupli 是否是重复接收的消息
     * @return
     */
    private boolean ifAck(MessageWrapper messageWrapper,boolean dupli) {
        VitalProtobuf.Protocol message = (VitalProtobuf.Protocol) messageWrapper.getMessage();
        VitalProtobuf.DataType dataType = message.getDataType();
        if (dataType == VitalProtobuf.DataType.AckMessageType) {
            if (!dupli) {
                VitalProtobuf.AckMessage ackMessage = message.getAckMessage();
                //callback
                VitalMessageWrapper vitalMessageWrapper = new VitalMessageWrapper(message);
                callBack(vitalMessageWrapper);

                //qos ,移除ack对应的发送的消息，并且添加ack到接受消息队列
                clientSendQos.remove(vitalMessageWrapper.getAckQosId());
                clientReceiveQos.addIfAbsent(vitalMessageWrapper.getQosId(),vitalMessageWrapper);
            }

            return true;
        } else if (dataType == VitalProtobuf.DataType.AckMessageWithExtraType) {
            if (!dupli) {
                VitalProtobuf.AckMessageWithExtra ackMessageWithExtra = message.getAckMessageWithExtra();
                //callback
                VitalMessageWrapper vitalMessageWrapper = new VitalMessageWrapper(message, ackMessageWithExtra.getAckPerId(), ackMessageWithExtra.getAckTimeStamp());
                callBack(vitalMessageWrapper);

                //qos ,移除ack对应的发送的消息，并且添加ack到接受消息队列
                clientSendQos.remove(vitalMessageWrapper.getAckQosId());
                clientReceiveQos.addIfAbsent(vitalMessageWrapper.getQosId(),vitalMessageWrapper);
            }
            return true;
        }

        return false;
    }

    /**
     * 消息回调
     * @param vitalMessageWrapper
     */
    protected void callBack(VitalMessageWrapper vitalMessageWrapper) {
        sender.invokeCallback(vitalMessageWrapper);
    }


    /**
     * 检查是否是ack
     * @param message
     * @return 是返回true，否则返回null
     */
    private boolean checkWhetherAck(VitalProtobuf.Protocol message) {
        VitalProtobuf.DataType dataType = message.getDataType();
        if (dataType == VitalProtobuf.DataType.AckMessageType) {
            return true;
        } else if (dataType == VitalProtobuf.DataType.AckMessageWithExtraType) {
            return true;
        }
        return false;
    }

    /**
     * 发送ack
     * @param defaultMessageContext
     * @param messageWrapper
     * @param dupli 消息是否重复
     */
    private void ackMsg(DefaultMessageContext defaultMessageContext, MessageWrapper messageWrapper, boolean dupli) {
        //不需要qos,或者是ack，ack不需要再ack，禁止套娃，上一步ack已经过滤
        if(!messageWrapper.getQos()){
            return;
        }
        //加入ReceiveQos，防止qos导致消息重复
        if (!dupli) {
            clientReceiveQos.addIfAbsent(messageWrapper.getQosId(),messageWrapper);
        }
        VitalProtobuf.Protocol ack = null;
        //ack是否携带id和时间戳
        if(!messageWrapper.getAckExtra()) {
             ack= ClientVitalMessageFactory.createAck((VitalProtobuf.Protocol) messageWrapper.getMessage());
        }else {
            ack = ClientVitalMessageFactory.createAckWithExtra((VitalProtobuf.Protocol) messageWrapper.getMessage(), messageWrapper.getAckPerId(), messageWrapper.getAckTimeStamp());
        }
        //不管消息重不重复，都发ack
        VitalClientSendHelper.send(defaultMessageContext.getChannelHandlerContext().channel(),ack);
        log.warn("发送ack,对应消息的qosId{}",messageWrapper.getQosId());
    }


    /**
     * 检查消息是不是重复
     * @param message
     * @return 如果消息重复，返回之前第一次收到的消息，否则返回null
     */
    private MessageWrapper checkMsgWhetherDuplication(VitalProtobuf.Protocol message) {
        log.debug("收到消息：{},qosId:{}",message.toString(),message.getQosId());
        if (message.getQos()) {
            String qosId = message.getQosId();
            MessageWrapper messageWrapper = clientReceiveQos.getIfHad(qosId);
            return messageWrapper;
        }
        return null;
    }


}
