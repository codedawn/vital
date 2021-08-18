package com.codedawn.vital.proto;


import io.netty.channel.Channel;

/**
 * @author codedawn
 * @date 2021-07-25 21:17
 */
public interface MessageWrapper {
    /**
     * 获取已经重发次数
     * @return
     */
    Integer getRetryCount();

    /**
     * 获取时间戳
     * @return
     */
    Long getTimeStamp();

    /**
     * 增加1重发次数
     * @return
     */
    VitalMessageWrapper increaseRetryCount();

    /**
     * 获取qosId
     * @return
     */
    String getQosId();

    /**
     * 获取消息
     * @return
     */
    public Object getMessage();

    /**
     * 获取qos，是否开启qos
     * @return
     */
    boolean getQos();

    /**
     * AckExtra中的AckPerId
     * @return
     */
    String getAckPerId();

    /**
     * AckExtra是ack的一种类型
     * @return
     */
    boolean getAckExtra();

    /**
     * AckExtra中的AckTimeStamp
     * @return
     */
    Long getAckTimeStamp();
    /**
     * 注意与{@link MessageWrapper#getQosId()}区分，ack的消息里边的AckQosId，也就是对应一个消息的qosId
     * @return
     */
    String getAckQosId();

    /**
     * 消息里边的ExceptionQosId，也就是对应一个消息的qosId
     * @return
     */
    String getExceptionQosId();

    Channel getChannel();

    MessageWrapper setChannel(Channel channel);
}
