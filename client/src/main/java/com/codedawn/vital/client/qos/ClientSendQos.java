package com.codedawn.vital.client.qos;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.client.connector.Sender;
import com.codedawn.vital.server.callback.TimeoutMessageCallBack;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.qos.SendQos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-23 21:40
 */
public class ClientSendQos extends SendQos {

    private static Logger log = LoggerFactory.getLogger(ClientSendQos.class);

    private Sender sender;

    public ClientSendQos() {
    }

    public ClientSendQos setSender(Sender sender) {
        this.sender = sender;
        return this;
    }

    @Override
    protected boolean checkWhetherRetry(int retryCount) {
        if(retryCount >= ClientVitalGenericOption.SEND_QOS_MAX_RETRY_COUNT.value()){
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected boolean checkWhetherExpire(long toNow) {
        if (toNow >= ClientVitalGenericOption.SEND_QOS_MAX_DELAY_TIME.value()) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void reSend(MessageWrapper messageWrapper) {
        log.info("seq:{}消息重传", messageWrapper.getSeq());
        sender.send(messageWrapper);
    }



    public void clear(){
        messages.clear();
        this.count.set(0);
        this.reSendCount.set(0);
    }


    @Override
    public ClientSendQos setTimeoutMessageCallBack(TimeoutMessageCallBack timeoutMessageCallBack) {
        this.timeoutMessageCallBack = timeoutMessageCallBack;
        return this;
    }
}
