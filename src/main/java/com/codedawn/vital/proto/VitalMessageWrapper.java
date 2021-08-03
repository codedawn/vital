package com.codedawn.vital.proto;

import com.codedawn.vital.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-23 21:56
 */
public class VitalMessageWrapper implements MessageWrapper {

    private static Logger log = LoggerFactory.getLogger(VitalMessageWrapper.class);
    /**
     * 消息载体
     */
    private VitalProtobuf.Protocol message;
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
     * 服务器传回来的persistent ID，可以用于持久化
     */
    private String ackPerId;



    /**
     * 接收ackWithExtra消息时使用
     * @param message
     * @param ackPerId
     * @param ackTimeStamp
     */
    public VitalMessageWrapper(VitalProtobuf.Protocol message, String ackPerId, Long ackTimeStamp) {
        this(message);
        this.ackTimeStamp = ackTimeStamp;
        this.ackPerId = ackPerId;
    }


    /**
     * 保证发送消息时使用
     * @param message
     */
    public VitalMessageWrapper(VitalProtobuf.Protocol message) {
        this.message = message;
        this.timeStamp = System.currentTimeMillis();
        this.retryCount = 0;
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
    public String getQosId() {
        return message.getQosId();
    }

    @Override
    public VitalProtobuf.Protocol getMessage() {
        return message;
    }

    @Override
    public boolean getQos() {
        return message.getQos();
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
        return message.getAckExtra();
    }

    @Override
    public String getAckQosId() {
        if (message.getAckMessage() == null) {
            log.info("message.getAckMessage() return  null");
            return null;
        }
        return message.getAckMessage().getAckQosId();
    }
    @Override
    public String getExceptionQosId() {
        if (message.getExceptionMessage() == null) {
            log.info("message.getExceptionMessage() return  null");
            return null;
        }
        return message.getExceptionMessage().getExceptionQosId();
    }

    @Override
    public String toString() {
        return "VitalMessageWrapper{" +
                "message=" + message +
                ", timeStamp=" + timeStamp +
                ", retryCount=" + retryCount +
                ", perId='" + ackPerId + '\'' +
                '}';
    }
}
