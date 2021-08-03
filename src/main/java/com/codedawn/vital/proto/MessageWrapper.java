package com.codedawn.vital.proto;

/**
 * @author codedawn
 * @date 2021-07-25 21:17
 */
public interface MessageWrapper {
    Integer getRetryCount();

    Long getTimeStamp();

    VitalMessageWrapper increaseRetryCount();

    String getQosId();

    public Object getMessage();

    boolean getQos();

    String getAckPerId();

    boolean getAckExtra();

    Long getAckTimeStamp();
    /**
     * 注意与{@link MessageWrapper#getQosId()}区分
     * @return
     */
    String getAckQosId();

    String getExceptionQosId();
}
