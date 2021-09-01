package com.codedawn.vital.server.proto;

import com.codedawn.vital.server.util.StringUtils;
import com.google.protobuf.Descriptors;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * @author codedawn
 * @date 2021-07-23 21:56
 */
public class VitalMessageWrapper implements MessageWrapper<VitalPB.Protocol> {

    private static Logger log = LoggerFactory.getLogger(VitalMessageWrapper.class);
    /**
     * 消息协议
     */
    private VitalPB.Protocol protocol;
    /**
     * 发送时间
     */
    private Long timeStamp ;

    /**
     * 服务器收到时间，会通过ackMessage返回
     */
    private Long ackTimeStamp ;
    /**
     * 发送时重发次数
     */
    private Integer retryCount ;
    /**
     * 服务器生成的persistent ID，可以用于持久化
     */
    private String ackPerId;

    private Channel channel;


    /**
     * 接收ackWithExtra消息时使用
     * @param protocol
     * @param ackPerId
     * @param ackTimeStamp
     */
    public VitalMessageWrapper(VitalPB.Protocol protocol, String ackPerId, Long ackTimeStamp) {
        this(protocol);
        this.ackTimeStamp = ackTimeStamp;
        this.ackPerId = ackPerId;
    }


    /**
     * 保证发送消息时使用
     * @param protocol
     */
    public VitalMessageWrapper(VitalPB.Protocol protocol) {
        this.protocol = protocol;
        this.timeStamp = System.currentTimeMillis();
        this.retryCount = 0;
    }


    /**
     * 根据泛型获取消息，没有则返回null
     * @param <E>
     * @return
     */
    @Override
    public <E> E getMessage(){
        VitalPB.Protocol protocol = getProtocol();
        VitalPB.MessageType messageType = protocol.getBody().getMessageType();
        E e;
        Map<Descriptors.FieldDescriptor, Object> allFields = protocol.getAllFields();
        for (Map.Entry<Descriptors.FieldDescriptor, Object> next : allFields.entrySet()) {
            try {
                e = (E) next.getValue();
                return e;
            } catch (Exception exception) {

            }
        }
        return null;
    }


    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public VitalMessageWrapper setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public Long getTimeStamp() {
        if (timeStamp == null || timeStamp == 0) {
            return 0L;
        }
        return timeStamp;
    }
    @Override
    public Long getAckTimeStamp() {
        if (ackTimeStamp == null || ackTimeStamp == 0) {
            log.info("ackTimeStamp启用ackWithExtra时才有意义");
            return 0L;
        }
        return ackTimeStamp;
    }

    public VitalMessageWrapper setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    @Override
    public Integer getRetryCount() {
        return retryCount;
    }

    public VitalMessageWrapper setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    @Override
    public VitalMessageWrapper increaseRetryCount() {
        this.retryCount += 1;
        return this;
    }

    @Override
    public String getSeq() {
        return protocol.getHeader().getSeq();
    }

    @Override
    public VitalPB.Protocol getProtocol() {
        return protocol;
    }

    @Override
    public boolean getIsQos() {
        return protocol.getHeader().getIsQos();
    }

    @Override
    public String getAckPerId() {
        if (StringUtils.isEmpty(ackPerId)) {
            log.info("perId启用ackWithExtra时才有意义");
            return null;
        }
        return ackPerId;
    }

    @Override
    public boolean getAckExtra() {
        return protocol.getAckExtra();
    }

    @Override
    public String getAckQosId() {
        if (protocol.getAckMessage() == null) {
            log.info("message.getAckMessage() return  null");
            return null;
        }
        return protocol.getAckMessage().getAckQosId();
    }
    @Override
    public String getExceptionQosId() {
        if (protocol.getExceptionMessage() == null) {
            log.info("message.getExceptionMessage() return  null");
            return null;
        }
        return protocol.getExceptionMessage().getExceptionQosId();
    }

    @Override
    public String toString() {
        return "VitalMessageWrapper{" +
                "message=" + protocol +
                ", timeStamp=" + timeStamp +
                ", retryCount=" + retryCount +
                ", perId='" + ackPerId + '\'' +
                '}';
    }
}
