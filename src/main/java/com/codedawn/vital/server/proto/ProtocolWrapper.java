package com.codedawn.vital.server.proto;

/**
 * @author codedawn
 * @date 2021-07-23 21:56
 */
public class ProtocolWrapper {
    private VitalProtocol.Protocol protocol;
    private Long timeStamp = 0L;
    private Integer retryCount = 0;


    public ProtocolWrapper(VitalProtocol.Protocol protocol, Long timeStamp) {
        this.protocol = protocol;
        this.timeStamp = timeStamp;
    }

    public VitalProtocol.Protocol getProtocol() {
        return protocol;
    }

    public ProtocolWrapper setProtocol(VitalProtocol.Protocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public ProtocolWrapper setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public ProtocolWrapper setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public ProtocolWrapper increaseRetryCount() {
        this.retryCount += 1;
        return this;
    }
}
