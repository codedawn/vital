package com.codedawn.vital.server.proto;

import com.google.protobuf.Descriptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-23 21:56
 */
public class VitalMessageWrapper implements MessageWrapper {

    private static Logger log = LoggerFactory.getLogger(VitalMessageWrapper.class);
    /**
     * 消息协议
     */
    private VitalPB.Frame frame;
    /**
     * 发送时间
     */
    private Long qosTime;

    /**
     * 发送时重发次数
     */
    private Integer retryCount ;
    /**
     * 服务器生成的persistent ID，可以用于持久化
     */



    /**
     * 接收ackWithExtra消息时使用
     * @param frame
     * @param perId
     */
    public VitalMessageWrapper(VitalPB.Frame frame, String perId) {
        this.qosTime = System.currentTimeMillis();
        this.retryCount = 0;
        if(frame.getHeader().getIsAckExtra()){
            VitalPB.Header.Builder builder = frame.getHeader().toBuilder();
            builder.setPerId(perId)
                    .setTimestamp(this.qosTime);
            VitalPB.Frame.Builder f = frame.toBuilder().setHeader(builder);
            frame=f.build();
        }
        this.frame = frame;
    }


    /**
     * 保证发送消息时使用
     * @param frame
     */
    public VitalMessageWrapper(VitalPB.Frame frame) {
        this.qosTime = System.currentTimeMillis();
        this.retryCount = 0;
        this.frame = frame;
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
        VitalPB.Body body = getFrame().getBody();
        Descriptors.FieldDescriptor oneofFieldDescriptor = body.getOneofFieldDescriptor(VitalPB.Body.getDescriptor().getOneofs().get(0));
        return (E) body.getField(oneofFieldDescriptor);
    }




    @Override
    public Long getQosTime() {
        if (qosTime == null) {
            return 0L;
        }
        return qosTime;
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
        return frame;
    }


    @Override
    public  void setFrame(Object protocol) {
        this.frame = (VitalPB.Frame) protocol;
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
    public String getPerId() {
        return getFrame().getHeader().getPerId();
    }

    @Override
    public Long getTimestamp() {
        return getFrame().getHeader().getTimestamp();
    }
}
