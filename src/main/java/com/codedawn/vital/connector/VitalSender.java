package com.codedawn.vital.connector;

import com.codedawn.vital.callback.ResponseCallBack;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.qos.SendQos;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author codedawn
 * @date 2021-07-25 8:53
 */
public class VitalSender implements Sender {


    private static Logger log = LoggerFactory.getLogger(VitalSender.class);

    private TCPConnect tcpConnect;

    private SendQos sendQos;

    /**
     * 消息回调，ack到达时调用
     */
    private ConcurrentHashMap<String, ResponseCallBack> messageCallBackMap = new ConcurrentHashMap<>();

    private ExecutorService defaultExecutor;

    public VitalSender(TCPConnect tcpConnect, SendQos sendQos) {
        this();
        this.tcpConnect = tcpConnect;
        this.sendQos = sendQos;
    }

    public VitalSender(SendQos sendQos) {
        this();
        this.sendQos = sendQos;
    }

    private VitalSender() {
        this.defaultExecutor = new ThreadPoolExecutor(1, 1, 60,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(500000), new DefaultThreadFactory(
                "vital-sender-executor", true), new ThreadPoolExecutor.DiscardPolicy());
    }

    /**
     *  查看消息是否可以通行，auth和heartbeat,ack可以未认证发送
     */
    private boolean checkPermit(Object msg) {
        if (msg instanceof VitalProtobuf.Protocol) {
            VitalProtobuf.DataType dataType = ((VitalProtobuf.Protocol) msg).getDataType();
            if (dataType == VitalProtobuf.DataType.AuthMessageType
                    || dataType == VitalProtobuf.DataType.HeartbeatType
                    ||dataType== VitalProtobuf.DataType.AckMessageType
                    ||dataType== VitalProtobuf.DataType.AckMessageWithExtraType) {
                return true;
            }
        }
        return false;
    }
    /**
     * 直接使用本方法，即时设置了qos也不起作用，应该使用{@link VitalSender#send(Object, ResponseCallBack)}
     * @param protocol
     */
    @Override
    public void send(Object protocol) {
        if (tcpConnect.getChannel() == null) {
            log.info("发送消息时，channel为null,说明未连接服务器,发送线程会自旋直到连接成功");
            while (tcpConnect.getChannel() == null) {

            }
        }

        VitalProtobuf.Protocol message = (VitalProtobuf.Protocol) protocol;
        //未认证并且是需要认证才能发送的消息
        if (!tcpConnect.isAuth()&&!checkPermit(protocol)) {
            log.info("发送的消息需要认证,当前未认证，该消息会直接丢弃");
            return;

        }
        VitalSendHelper.send(tcpConnect.getChannel(),message,sendQos);
    }

    /**
     *
     * @param protocol
     * @param responseCallBack 消息回调接口
     */
    @Override
    public void send(Object protocol, ResponseCallBack responseCallBack) {
        VitalProtobuf.Protocol message = (VitalProtobuf.Protocol) protocol;

        if (message.getQos()) {
            messageCallBackMap.putIfAbsent(message.getQosId(), responseCallBack);
        }else {
            log.info("设置了MessageCallBack，但是没有开启qos，所以永远不会调用MessageCallBack");
        }
        defaultExecutor.execute(new Runnable() {
            @Override
            public void run() {
                send(protocol);
            }
        });
    }

    /**
     * ack到达，调用消息回调
     * @param messageWrapper
     */
    @Override
    public void invokeCallback(Object messageWrapper) {
        VitalMessageWrapper message = (VitalMessageWrapper) messageWrapper;
        ResponseCallBack responseCallBack = messageCallBackMap.get(message.getAckQosId());
        if (responseCallBack != null) {
            defaultExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    responseCallBack.ackArrived(message);
                }
            });

        }
    }

    /**
     * 异常到达，调用消息回调
     * @param messageWrapper
     */
    @Override
    public void invokeExceptionCallback(Object messageWrapper) {
        VitalMessageWrapper message = (VitalMessageWrapper) messageWrapper;
        ResponseCallBack responseCallBack = messageCallBackMap.get(message.getExceptionQosId());
        if (responseCallBack != null) {
            defaultExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    responseCallBack.exception(message);
                }
            });

        }
    }

    @Override
    public VitalSender setTcpConnect(TCPConnect tcpConnect) {
        this.tcpConnect = tcpConnect;
        return this;
    }
}
