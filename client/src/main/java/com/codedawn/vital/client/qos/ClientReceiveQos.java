package com.codedawn.vital.client.qos;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.qos.ReceiveQos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * @author codedawn
 * @date 2021-07-25 23:09
 *
 * {@link MessageWrapper#getIsQos()}是指消息会不会加入{@link ClientSendQos},而不是会不会加入{@link ClientReceiveQos}
 * 所以说，所有的消息都会加入{@link ClientReceiveQos}，冗余的消息只会有一份（去重），这就是{@link ClientReceiveQos}的作用。
 *
 * 举个例子：
 * A端要向B端发一个消息：
 * 1.A发送一个消息，并开启qos，也就是{@link MessageWrapper#getIsQos()}会返回true，会加入{@link ClientSendQos}
 * 2.B接受到消息，并回复ack
 * 3.由于网络延迟，A没有收到来自B的ack，所以重发该消息
 * 4.B再次收到A发来的消息，并回复ack
 * 5.第一次和第二次发的ack都到达A。
 *
 * 在这个过程中，A接受到了两个ack，B收到了两个相同消息，所以需要去重，这就是{@link ClientReceiveQos}存在的原因
 */
public class ClientReceiveQos extends ReceiveQos {

    private static Logger log = LoggerFactory.getLogger(ClientReceiveQos.class);

//    private ConcurrentHashMap<String, MessageWrapper> receiveMessages = new ConcurrentHashMap<>();

    private ClientSendQos sendQos;

//    private AtomicInteger count = new AtomicInteger(0);

    public ClientReceiveQos() {
    }

    @Override
    public void checkTask() {

        log.warn("开始检测接受到的消息 qos");
        Iterator<Map.Entry<String, MessageWrapper>> iterator = receiveMessages.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, MessageWrapper> entry = iterator.next();
            Long timeStamp = entry.getValue().getQosTime();
            long toNow = System.currentTimeMillis() - (timeStamp < 0 ? 0 : timeStamp);
            if (toNow >= ClientVitalGenericOption.RECEIVE_QOS_MAX_SAVE_TIME.value()) {
                sendQos.deleteCallBack(entry.getValue().getSeq());
                iterator.remove();
                continue;
            }

        }

        log.info("ReceiveQos消息队列长度：{}",receiveMessages.size());
        log.info("ReceiveQos接收消息总数消息{}",count.get());

    }


    public ClientReceiveQos setSendQos(ClientSendQos sendQos) {
        this.sendQos = sendQos;
        return this;
    }
}
