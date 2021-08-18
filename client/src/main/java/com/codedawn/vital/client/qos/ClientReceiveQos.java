package com.codedawn.vital.client.qos;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.server.proto.MessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author codedawn
 * @date 2021-07-25 23:09
 *
 * {@link MessageWrapper#getQos()}是指消息会不会加入{@link ClientSendQos},而不是会不会加入{@link ClientReceiveQos}
 * 所以说，所有的消息都会加入{@link ClientReceiveQos}，冗余的消息只会有一份（去重），这就是{@link ClientReceiveQos}的作用。
 *
 * 举个例子：
 * A端要向B端发一个消息：
 * 1.A发送一个消息，并开启qos，也就是{@link MessageWrapper#getQos()}会返回true，会加入{@link ClientSendQos}
 * 2.B接受到消息，并回复ack
 * 3.由于网络延迟，A没有收到来自B的ack，所以重发该消息
 * 4.B再次收到A发来的消息，并回复ack
 * 5.第一次和第二次发的ack都到达A。
 *
 * 在这个过程中，A接受到了两个ack，B收到了两个相同消息，所以需要去重，这就是{@link ClientReceiveQos}存在的原因
 */
public class ClientReceiveQos {

    private static Logger log = LoggerFactory.getLogger(ClientReceiveQos.class);

    private ConcurrentHashMap<String, MessageWrapper> receiveMessages = new ConcurrentHashMap<>();




    private AtomicInteger count = new AtomicInteger(0);

    public ClientReceiveQos() {
    }

    public void checkTask() {

        log.warn("开始检测接受到的消息 qos");
        Iterator<Map.Entry<String, MessageWrapper>> iterator = receiveMessages.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, MessageWrapper> entry = iterator.next();
            MessageWrapper value = entry.getValue();
            Long timeStamp = value.getTimeStamp();
            long toNow = System.currentTimeMillis() - (timeStamp < 0 ? 0 : timeStamp);
            if (toNow >= ClientVitalGenericOption.RECEIVE_QOS_MAX_SAVE_TIME.value()) {
                iterator.remove();
                continue;
            }

        }

        log.info("ReceiveQos消息队列长度：{}",receiveMessages.size());
        log.info("ReceiveQos接收消息总数消息{}",count.get());

    }


    public void addIfAbsent(String qosId, MessageWrapper messageWrapper) {
        MessageWrapper m = receiveMessages.putIfAbsent(qosId, messageWrapper);
        if (m == null) {
            if (log.isInfoEnabled()) {
                count.incrementAndGet();
            }
        }

    }

    public boolean hasMessage(String qosId) {
        return receiveMessages.containsKey(qosId);
    }

    public MessageWrapper getIfHad(String qosId) {
        return receiveMessages.get(qosId);
    }

}
