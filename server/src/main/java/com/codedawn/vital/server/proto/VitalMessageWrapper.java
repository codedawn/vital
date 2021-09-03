package com.codedawn.vital.server.proto;

import com.google.protobuf.Descriptors;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author codedawn
 * @date 2021-07-23 21:56
 */
public class VitalMessageWrapper implements MessageWrapper {

    private static Logger log = LoggerFactory.getLogger(VitalMessageWrapper.class);
    /**
     * 消息协议
     */
    private VitalPB.Frame protocol;
    /**
     * 发送时间
     */
    private Long timeStamp ;

    /**
     * 发送时重发次数
     */
    private Integer retryCount ;
    /**
     * 服务器生成的persistent ID，可以用于持久化
     */
    private String perId;

    private Channel channel;


    /**
     * 接收ackWithExtra消息时使用
     * @param protocol
     * @param perId
     */
    public VitalMessageWrapper(VitalPB.Frame protocol, String perId) {
        this(protocol);
        this.perId = perId;
    }


    /**
     * 保证发送消息时使用
     * @param protocol
     */
    public VitalMessageWrapper(VitalPB.Frame protocol) {
        this.protocol = protocol;
        this.timeStamp = System.currentTimeMillis();
        this.retryCount = 0;
    }



    @Override
    public String getToId() {
        return getFrame().getHeader().getToId();
    }

    @Override
    public String getFromId() {
        return getFrame().getHeader().getFromId();
    }
    /**
     * 是否群发
     * @return
     */
    @Override
    public boolean getIsGroup() {
        return getFrame().getHeader().getIsGroup();
    }

    /**
     * 根据泛型获取消息，没有则返回null
     * @param <E>
     * @return
     */
    @Override
    public <E> E getMessage(){
        VitalPB.Frame protocol = getFrame();
        E e;
        Map<Descriptors.FieldDescriptor, Object> allFields = protocol.getBody().getAllFields();
        ListIterator<Map.Entry<Descriptors.FieldDescriptor, Object>> li = new ArrayList<Map.Entry<Descriptors.FieldDescriptor, Object>>(allFields.entrySet()).listIterator(allFields.size());
        while(li.hasPrevious()){
            Map.Entry<Descriptors.FieldDescriptor, Object> previous = li.previous();
            try {
                e = (E) previous.getValue();
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
        return getFrame().getHeader().getSeq();
    }

    /**
     * 获取消息协议
     *
     * @return
     */
    @Override
    public VitalPB.Frame getFrame() {
        return  protocol;
    }


    @Override
    public  void setFrame(Object protocol) {
        this.protocol= (VitalPB.Frame) protocol;
    }

    @Override
    public boolean getIsQos() {
        return getFrame().getHeader().getIsQos();
    }



    @Override
    public boolean getIsAckExtra() {
        return getFrame().getHeader().getIsAckExtra();
    }





    @Override
    public String toString() {
        return "VitalMessageWrapper{" +
                "message=" + protocol +
                ", timeStamp=" + timeStamp +
                ", retryCount=" + retryCount +
                ", perId='" + perId + '\'' +
                '}';
    }


    @Override
    public String getPerId() {
        return perId;
    }
}
