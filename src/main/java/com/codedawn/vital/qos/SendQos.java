package com.codedawn.vital.qos;

import com.codedawn.vital.callback.TimeoutMessageCallBack;
import com.codedawn.vital.config.VitalGenericOption;
import com.codedawn.vital.connector.VitalSendHelper;
import com.codedawn.vital.proto.MessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import io.netty.channel.Channel;
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

    private ConcurrentHashMap<String, MessageWrapper> messages = new ConcurrentHashMap<>();


    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    private TimeoutMessageCallBack timeoutMessageCallBack;

    private boolean serverSide = true;



    public SendQos(boolean serverSide) {
        this.serverSide = serverSide;
    }

    private AtomicInteger count = new AtomicInteger(0);

    private AtomicInteger reSendCount = new AtomicInteger(0);

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
                Long timeStamp = messageWrapper.getTimeStamp();
                long toNow = System.currentTimeMillis() - timeStamp;

                /**
                 * ack等待MAX_DELAY_TIME毫秒后还没到达，进行重传
                 */
                if (toNow >= VitalGenericOption.SEND_QOS_MAX_DELAY_TIME.value()) {
                    count.incrementAndGet();
                    reSend(messageWrapper);
                    messageWrapper.increaseRetryCount();

                } else {
                    log.info("protocol:{} 上次发送至今为{}ms，不需要重传", messageWrapper.getQosId(),toNow);
                }
            }
        }
        if(timeoutMessages.size()>0) {
            timeoutMessageCallBack(timeoutMessages);
        }
        log.info("正在发送{}条消息",messages.size());
        log.info("丢失{}条消息",timeoutMessages.size());
        log.info("sendQos发送消息总数{}(包括重发)",count.get());
    }

    private void reSend(MessageWrapper messageWrapper) {
        Channel channel = messageWrapper.getChannel();
        if (channel != null) {
            VitalSendHelper.send(channel, (VitalProtobuf.Protocol) messageWrapper.getMessage());
        }

    }
    public void timeoutMessageCallBack(ArrayList<MessageWrapper> timeoutMessages) {
        if (timeoutMessageCallBack != null) {
            timeoutMessageCallBack.timeout(timeoutMessages);
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

    public void addIfAbsent(String qosId, MessageWrapper messageWrapper) {
        MessageWrapper m = messages.putIfAbsent(qosId, messageWrapper);
        if (m == null) {
            count.incrementAndGet();
        }
    }

    public void remove(String qosId) {
        messages.remove(qosId);
    }

}
