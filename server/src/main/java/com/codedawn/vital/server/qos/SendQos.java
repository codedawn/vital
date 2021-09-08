package com.codedawn.vital.server.qos;

import com.codedawn.vital.server.callback.RequestSendCallBack;
import com.codedawn.vital.server.callback.SendCallBack;
import com.codedawn.vital.server.callback.TimeoutMessageCallBack;
import com.codedawn.vital.server.config.VitalGenericOption;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.VitalPB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author codedawn
 * @date 2021-07-23 21:40
 */
public class SendQos {

    private static Logger log = LoggerFactory.getLogger(SendQos.class);

    protected ConcurrentHashMap<String, MessageWrapper> messages = new ConcurrentHashMap<>();


    protected ConcurrentHashMap<String, SendCallBack> messageCallBackMap = new ConcurrentHashMap<>();

    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    protected TimeoutMessageCallBack timeoutMessageCallBack;

    private Protocol protocol;

    public SendQos() {
    }
    protected AtomicInteger count = new AtomicInteger(0);

    protected AtomicInteger reSendCount = new AtomicInteger(0);

    private void checkTask() {

//        log.info("开始检测是否需要重传 qos");
        ArrayList<MessageWrapper> timeoutMessages = new ArrayList<>();

        Iterator<Map.Entry<String, MessageWrapper>> iterator = messages.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, MessageWrapper> entry = iterator.next();
            MessageWrapper messageWrapper = entry.getValue();

            /**
             * protocol 重传次数到达最大
             */
            if (messageWrapper.getRetryCount() >= VitalGenericOption.SEND_QOS_MAX_RETRY_COUNT.value()) {
                timeoutMessages.add(messageWrapper);
                iterator.remove();
                continue;
            }else {
                Long timeStamp = messageWrapper.getQosTime();
                long toNow = System.currentTimeMillis() - timeStamp;

                /**
                 * ack等待MAX_DELAY_TIME毫秒后还没到达，进行重传
                 */
                if (toNow >= VitalGenericOption.SEND_QOS_MAX_DELAY_TIME.value()) {
                    if (log.isInfoEnabled()) {
                        reSendCount.incrementAndGet();
                    }
                    reSend(messageWrapper);
                    messageWrapper.increaseRetryCount();

                } else {
                    log.info("seq:{} 上次发送至今为{}ms，不需要重传", messageWrapper.getSeq(),toNow);
                }
            }
        }
        if(timeoutMessages.size()>0) {
            timeoutMessageCallBack(timeoutMessages);
        }
        log.info("正在发送{}条消息",messages.size());
        log.info("丢失{}条消息",timeoutMessages.size());
        log.info("sendQos发送消息数{}，重发消息数{}",count.get(),reSendCount.get());
    }

    private void reSend(MessageWrapper messageWrapper) {
        log.info("seq:{}消息重传", messageWrapper.getSeq());
        protocol.send(messageWrapper.getToId(),messageWrapper);

    }
    public void timeoutMessageCallBack(ArrayList<MessageWrapper> timeoutMessages) {
        if (timeoutMessageCallBack != null) {
            timeoutMessageCallBack.onTimeout(timeoutMessages);
        }

    }

    public SendQos setTimeoutMessageCallBack(TimeoutMessageCallBack timeoutMessageCallBack) {
        this.timeoutMessageCallBack = timeoutMessageCallBack;
        return this;
    }

    public void start() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    checkTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },0,  VitalGenericOption.SEND_QOS_INTERVAL_TIME.value(), TimeUnit.MILLISECONDS);

    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        executorService = null;
    }

    public void addMessageIfAbsent(String seq, MessageWrapper messageWrapper) {
        MessageWrapper m = messages.putIfAbsent(seq, messageWrapper);
        if (m == null) {
            if (log.isInfoEnabled()) {
                count.incrementAndGet();
            }
        }
    }

    public void removeMessage(String seq) {
        messages.remove(seq);
    }

    public SendQos setProtocol(Protocol protocol) {
        this.protocol = protocol;
        return this;
    }


    /**
     * ack到达，调用消息回调
     * @param messageWrapper
     */
    public void invokeAckCallback(MessageWrapper messageWrapper) {
        VitalPB.AckMessage ackMessage = messageWrapper.getMessage();
        String ackSeq=ackMessage.getAckSeq();
        SendCallBack sendCallBack = messageCallBackMap.get(ackSeq);
        if (sendCallBack != null) {
            MessageWrapper wrapper = messages.get(ackSeq);
            if(wrapper==null)return;
            if(wrapper.getIsAckExtra()){
                VitalPB.Frame frame = wrapper.getFrame();
                VitalPB.Header.Builder header = frame.getHeader().toBuilder();
                header.setTimestamp(messageWrapper.getTimestamp())
                        .setPerId(messageWrapper.getPerId());
                VitalPB.Frame.Builder builder = frame.toBuilder().setHeader(header);
                wrapper.setFrame(builder.build());
            }

            sendCallBack.onAck(wrapper);
        }
    }

    /**
     * 异常到达，调用消息回调
     * @param exception
     */
    public void invokeExceptionCallback(MessageWrapper exception) {
        VitalPB.ExceptionMessage exceptionMessage = exception.getMessage();
        SendCallBack sendCallBack = messageCallBackMap.get(exceptionMessage.getExceptionSeq());
        if (sendCallBack != null) {
            sendCallBack.onException(exception);

        }
    }

    public void invokeResponseCallBack(String seq,MessageWrapper response){
        SendCallBack sendCallBack = messageCallBackMap.get(seq);
        if(sendCallBack!=null&&sendCallBack instanceof RequestSendCallBack){
            ((RequestSendCallBack) sendCallBack).onResponse(response);
        }
    }



    public void putCallBackIfAbsent(String seq, SendCallBack sendCallBack){
        messageCallBackMap.putIfAbsent(seq, sendCallBack);
    }

    public void deleteCallBack(String seq){
        messageCallBackMap.remove(seq);
    }
}
