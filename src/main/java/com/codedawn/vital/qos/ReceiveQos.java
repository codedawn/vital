package com.codedawn.vital.qos;

import com.codedawn.vital.proto.MessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author codedawn
 * @date 2021-07-25 23:09
 *
 * {@link MessageWrapper#getQos()}是指消息会不会加入{@link SendQos},而不是会不会加入{@link ReceiveQos}
 * 所以说，所有的消息都会加入{@link ReceiveQos}，冗余的消息只会有一份（去重），这就是{@link ReceiveQos}的作用。
 *
 * 举个例子：
 * A端要向B端发一个消息：
 * 1.A发送一个消息，并开启qos，也就是{@link MessageWrapper#getQos()}会返回true，会加入{@link SendQos}
 * 2.B接受到消息，并回复ack
 * 3.由于网络延迟，A没有收到来自B的ack，所以重发该消息
 * 4.B再次收到A发来的消息，并回复ack
 * 5.第一次和第二次发的ack都到达A。
 *
 * 在这个过程中，A接受到了两个ack，B收到了两个相同消息，所以需要去重，这就是{@link ReceiveQos}存在的原因
 */
public class ReceiveQos {

    private static Logger log = LoggerFactory.getLogger(ReceiveQos.class);

    private ConcurrentHashMap<String, MessageWrapper> receiveMessages = new ConcurrentHashMap<>();


    private static final int INTERVAL_TIME = 20*1000;

    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    private static final int MAX_SAVE_TIME=3*60*1000;



    public ReceiveQos() {

    }

    private void checkTask() {

//        log.info("开始检测接受到的消息 qos");
        Iterator<Map.Entry<String, MessageWrapper>> iterator = receiveMessages.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, MessageWrapper> entry = iterator.next();
            MessageWrapper value = entry.getValue();
            Long timeStamp = value.getTimeStamp();
            long toNow = System.currentTimeMillis() - (timeStamp < 0 ? 0 : timeStamp);
            if (toNow > MAX_SAVE_TIME) {
                iterator.remove();
                continue;
            }

        }

        log.info("ReceiveQos消息队列长度：{}",receiveMessages.size());
    }


    public void start() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                checkTask();
            }
        },0,INTERVAL_TIME, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        executorService = null;
    }

    public void addIfAbsent(String qosId, MessageWrapper messageWrapper) {
        receiveMessages.putIfAbsent(qosId, messageWrapper);
    }

    public boolean hasMessage(String qosId) {
        return receiveMessages.containsKey(qosId);
    }

    public MessageWrapper getIfHad(String qosId) {
        return receiveMessages.get(qosId);
    }

}
