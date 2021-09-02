package com.codedawn.vital.server.proto;

import io.netty.channel.Channel;

/**
 * @author codedawn
 * @date 2021-07-25 21:17
 */
public interface MessageWrapper{

    String getToId();


    boolean getIsGroup();
    /**
     * 根据泛型获取消息，没有则返回null
     * @param <E>
     * @return
     */
     <E> E getMessage();
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
     <T> T getProtocol();

     void  setProtocol(Object protocol);

    /**
     * 是否需要qos
     * @return 如果需要qos返回true，否则返回false
     */
    boolean getIsQos();


    /**
     * 是否使用AckExtra，是ack的一种类型
     * @return
     */
    boolean getIsAckExtra();

    String getPerId();

    Channel getChannel();

    MessageWrapper setChannel(Channel channel);
}
