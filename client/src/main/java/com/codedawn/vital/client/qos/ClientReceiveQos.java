package com.codedawn.vital.client.qos;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.qos.ReceiveQos;

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


    public ClientReceiveQos() {
    }

    @Override
    protected boolean checkWhetherExpire(long toNow) {
        if (toNow >= ClientVitalGenericOption.RECEIVE_QOS_MAX_SAVE_TIME.value()) {
            return true;
        } else {
            return false;
        }
    }


    public void clear(){
        receiveMessages.clear();
        this.count.set(0);
    }

}
