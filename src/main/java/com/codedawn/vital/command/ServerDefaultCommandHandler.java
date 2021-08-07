package com.codedawn.vital.command;

import com.codedawn.vital.callback.MessageCallBack;
import com.codedawn.vital.callback.ResponseCallBack;
import com.codedawn.vital.connector.VitalSendHelper;
import com.codedawn.vital.context.DefaultMessageContext;
import com.codedawn.vital.factory.VitalMessageFactory;
import com.codedawn.vital.processor.Processor;
import com.codedawn.vital.processor.ProcessorManager;
import com.codedawn.vital.proto.MessageWrapper;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.qos.ReceiveQos;
import com.codedawn.vital.qos.SendQos;
import com.codedawn.vital.util.AddressUtil;
import com.codedawn.vital.util.SnowflakeIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author codedawn
 * @date 2021-07-24 22:30
 */
public class ServerDefaultCommandHandler implements CommandHandler<DefaultMessageContext> {

    private static Logger log = LoggerFactory.getLogger(ServerDefaultCommandHandler.class);

    private ProcessorManager processorManager;

    private ProcessorManager userProcessorManager;

    private ReceiveQos receiveQos;

    private SendQos sendQos;


    /**
     * 是否是服务端，客户端和服务端会不一样
     */
    protected boolean serverSide = true;


    private ResponseCallBack<VitalMessageWrapper> responseCallBack;

    private MessageCallBack messageCallBack;


    public ServerDefaultCommandHandler(ProcessorManager processorManager,ProcessorManager userProcessorManager, ReceiveQos receiveQos, SendQos sendQos,ResponseCallBack responseCallBack,MessageCallBack messageCallBack) {
        this.processorManager = processorManager;
        this.userProcessorManager = userProcessorManager;
        this.receiveQos = receiveQos;
        this.sendQos = sendQos;
        this.responseCallBack = responseCallBack;
        this.messageCallBack = messageCallBack;
    }


    @Override
    public void handle(DefaultMessageContext messageContext, Object msg) {
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
            messageCallBack.messageArrive(messageWrapper);
        }
    }
    protected MessageWrapper getMessageWrapper(VitalProtobuf.Protocol  message) {
        //不需要qos或者不需要ackExtra，设置ackTimestamp也没有意义
        if(!message.getQos()||!message.getAckExtra()){
            return new VitalMessageWrapper(message);
        }
         return new VitalMessageWrapper(message,String.valueOf(SnowflakeIdWorker.getInstance().nextId()), System.currentTimeMillis());
    }

    /**
     * 分发消息到processor
     * @param defaultMessageContext
     * @param messageWrapper
     */
    private void process(final DefaultMessageContext defaultMessageContext, final MessageWrapper messageWrapper) {


        VitalMessageWrapper vitalProtocolWrapper = (VitalMessageWrapper) messageWrapper;
        VitalProtobuf.Protocol p =  vitalProtocolWrapper.getMessage();
        String dataTypeStr = p.getDataType().toString();

        if (processorManager != null) {
            //派发到指定的Processor
            Processor processor = processorManager.getProcessor(dataTypeStr);
            if (processor != null) {
                ExecutorService executor = processor.getExecutor();
                if (executor == null) {
                    //如果processor没有指定线程池，就使用processorManage默认的线程池
                    executor=processorManager.getDefaultExecutor();
                }

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        processor.process(defaultMessageContext, messageWrapper);
                        log.info("processor执行完成{}",processor.toString());
                    }
                });
            }

        }


        if (userProcessorManager != null) {
            //派发到指定的userProcessor
            Processor userProcessor = userProcessorManager.getProcessor(dataTypeStr);
            if (userProcessor != null) {
                ExecutorService userProcessorExecutor = userProcessor.getExecutor();
                if (userProcessorExecutor == null) {
                    userProcessorExecutor=userProcessorManager.getDefaultExecutor();
                }
                userProcessorExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        userProcessor.process(defaultMessageContext,messageWrapper);
                    }
                });
            }

        }


    }
    /**
     * 检查是否是ack消息，如果是需要回调，还有移除{@link SendQos}中的对应消息，因为对方已经收到
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
                sendQos.remove(vitalMessageWrapper.getAckQosId());
                receiveQos.addIfAbsent(vitalMessageWrapper.getQosId(),vitalMessageWrapper);
            }

            return true;
        } else if (dataType == VitalProtobuf.DataType.AckMessageWithExtraType) {
            if (!dupli) {
                VitalProtobuf.AckMessageWithExtra ackMessageWithExtra = message.getAckMessageWithExtra();
                //callback
                VitalMessageWrapper vitalMessageWrapper = new VitalMessageWrapper(message, ackMessageWithExtra.getAckPerId(), ackMessageWithExtra.getAckTimeStamp());
                callBack(vitalMessageWrapper);

                //qos ,移除ack对应的发送的消息，并且添加ack到接受消息队列
                sendQos.remove(vitalMessageWrapper.getAckQosId());
                receiveQos.addIfAbsent(vitalMessageWrapper.getQosId(),vitalMessageWrapper);
            }
            return true;
        }

        return false;
    }

    protected void callBack(VitalMessageWrapper vitalMessageWrapper) {
        if (responseCallBack != null) {
            responseCallBack.ackArrived(vitalMessageWrapper);
        }
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
    private void ackMsg(DefaultMessageContext defaultMessageContext, MessageWrapper messageWrapper,boolean dupli) {
        //不需要qos,或者是ack，ack不需要再ack，禁止套娃，上一步ack已经过滤
        if(!messageWrapper.getQos()){
            return;
        }
        //加入ReceiveQos，防止qos导致消息重复
        if (!dupli) {
            receiveQos.addIfAbsent(messageWrapper.getQosId(),messageWrapper);
        }
        VitalProtobuf.Protocol ack = null;
        //ack是否携带id和时间戳
        if(!messageWrapper.getAckExtra()) {
             ack= VitalMessageFactory.createAck((VitalProtobuf.Protocol) messageWrapper.getMessage());
        }else {
            ack = VitalMessageFactory.createAckWithExtra((VitalProtobuf.Protocol) messageWrapper.getMessage(), messageWrapper.getAckPerId(), messageWrapper.getAckTimeStamp());
        }
        //不管消息重不重复，都发ack
        VitalSendHelper.send(defaultMessageContext.getChannelHandlerContext().channel(),ack);
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
            MessageWrapper messageWrapper = receiveQos.getIfHad(qosId);
            return messageWrapper;
        }
        return null;
    }


}
