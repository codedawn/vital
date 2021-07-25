package com.codedawn.vital.server.command;

import com.codedawn.vital.command.CommandHandler;
import com.codedawn.vital.context.MessageContext;
import com.codedawn.vital.factory.VitalMessageFactory;
import com.codedawn.vital.proto.MessageWrapper;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.qos.ReceiveQos;
import com.codedawn.vital.qos.SendQos;
import com.codedawn.vital.server.callback.ServerMessageCallBack;
import com.codedawn.vital.server.context.ServerMessageContext;
import com.codedawn.vital.server.processor.ProcessorManager;
import com.codedawn.vital.server.processor.ServerProcessor;
import com.codedawn.vital.server.util.SnowflakeIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author codedawn
 * @date 2021-07-24 22:30
 */
public class ServerDefaultCommandHandler implements CommandHandler {

    private static Logger log = LoggerFactory.getLogger(ServerDefaultCommandHandler.class);

    private ProcessorManager processorManager;

    private ReceiveQos receiveQos;

    private SendQos sendQos;

    //todo 消息发送前后的callback，还有转发
    private ServerMessageCallBack serverMessageCallBack;


    public ServerDefaultCommandHandler(ProcessorManager processorManager, ReceiveQos receiveQos, SendQos sendQos,ServerMessageCallBack serverMessageCallBack) {
        this.processorManager = processorManager;
        this.receiveQos = receiveQos;
        this.sendQos = sendQos;
        this.serverMessageCallBack = serverMessageCallBack;
    }


    @Override
    public void handle(MessageContext messageContext, Object msg) {
        ServerMessageContext serverMessageContext = (ServerMessageContext) messageContext;
        //已经接受过的消息，不放行
        MessageWrapper messageWrapper = checkMsgWhetherDuplication(msg);
        if (messageWrapper!=null) {
            this.ackMsg(serverMessageContext,messageWrapper);
            return;
        }else{
            //不是重复接收的消息发送ack
            //todo 默认雪花算法实现生成id，后续拓展可以自定义

            messageWrapper = new VitalMessageWrapper((VitalProtobuf.Protocol) msg, System.currentTimeMillis(), String.valueOf(SnowflakeIdWorker.getInstance().nextId()));
            this.ackMsg(serverMessageContext,messageWrapper);
        }
        //todo 服务器处理ack
        //如果是没有重复接收的ack，也就是第一次到达的ack
        if (ifAck(messageWrapper)) {
            //是ackMessage，可以return了
            return;
        }
        //接下来就是进行转发的消息

        this.process(serverMessageContext,messageWrapper);
    }

    private void process(final ServerMessageContext serverMessageContext, final MessageWrapper messageWrapper) {


        VitalMessageWrapper vitalProtocolWrapper = (VitalMessageWrapper) messageWrapper;
        VitalProtobuf.Protocol p =  vitalProtocolWrapper.getMessage();
        String dataTypeStr = p.getDataType().toString();


        ServerProcessor processor = processorManager.getProcessor(dataTypeStr);
        ExecutorService executor = processor.getExecutor();
        if (executor == null) {
            executor=processorManager.getDefaultExecutor();
        }

        executor.submit(new Runnable() {
            @Override
            public void run() {
                processor.process(serverMessageContext, messageWrapper);
            }
        });

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
            serverMessageCallBack.ackArrived(vitalMessageWrapper);

            //qos ,移除ack对应的发送的消息，并且添加ack到接受消息队列
            receiveQos.addIfAbsent(vitalMessageWrapper.getQosId(),vitalMessageWrapper);
            sendQos.remove(vitalMessageWrapper.getAckQosId());
            return true;
        } else if (dataType == VitalProtobuf.DataType.AckMessageWithExtraType) {
            VitalProtobuf.AckMessageWithExtra ackMessageWithExtra = message.getAckMessageWithExtra();
            //callback
            VitalMessageWrapper vitalMessageWrapper = new VitalMessageWrapper(message, ackMessageWithExtra.getAckPerId(), ackMessageWithExtra.getAckTimeStamp());
            serverMessageCallBack.ackArrived(vitalMessageWrapper);

            //qos ,移除ack对应的发送的消息，并且添加ack到接受消息队列
            receiveQos.addIfAbsent(vitalMessageWrapper.getQosId(),vitalMessageWrapper);
            sendQos.remove(vitalMessageWrapper.getAckQosId());
            return true;
        }

        return false;
    }
    /**
     *   检查是否是ack
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

    private void ackMsg(ServerMessageContext serverMessageContext, MessageWrapper messageWrapper) {
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
            ack = VitalMessageFactory.createAckWithExtra((VitalProtobuf.Protocol) messageWrapper.getMessage(), messageWrapper.getAckPerId(), messageWrapper.getTimeStamp());
        }
        serverMessageContext.getChannelHandlerContext().channel().writeAndFlush(ack);
    }


    private MessageWrapper checkMsgWhetherDuplication(Object msg) {
        VitalProtobuf.Protocol message = (VitalProtobuf.Protocol) msg;
        log.info("服务器收到消息：{}",message.toString());
        if (message.getQos()) {
            String qosId = message.getQosId();
            MessageWrapper messageWrapper = receiveQos.getIfHad(qosId);
            return messageWrapper;
        }
        return null;
    }


}
