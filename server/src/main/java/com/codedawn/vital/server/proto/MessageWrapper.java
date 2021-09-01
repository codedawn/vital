package com.codedawn.vital.server.proto;

import io.netty.channel.Channel;

/**
 * @author codedawn
 * @date 2021-07-25 21:17
 */
public interface MessageWrapper<T>{
    /**
     * 根据泛型获取消息，没有则返回null
     * @param <E>
     * @return
     */
    public <E> E getMessage();
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
    String getSeq();

    /**
     * 获取消息协议
     * @return
     */
    public T getProtocol();

    /**
     * 是否需要qos
     * @return 如果需要qos返回true，否则返回false
     */
    boolean getIsQos();

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
     * 注意与{@link MessageWrapper#getSeq()}区分，ack的消息里边的AckQosId，也就是对应一个消息的qosId
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
