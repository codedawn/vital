package com.codedawn.vital.client.processor.impl.client;

import com.codedawn.vital.client.qos.ClientSendQos;
import com.codedawn.vital.server.context.MessageContext;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.MessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * 收到异常消息使用该处理器，用于客户端
 * @author codedawn
 * @date 2021-07-29 16:04
 */
public class ExceptionProcessor implements Processor<MessageContext, MessageWrapper> {

    private static Logger log = LoggerFactory.getLogger(ExceptionProcessor.class);

    private ExecutorService executor;

    private ClientSendQos clientSendQos;

    public ExceptionProcessor() {
    }

    @Override
    public void process(MessageContext messageContext, MessageWrapper messageWrapper) {
        //触发消息回调
        clientSendQos.invokeExceptionCallback(messageWrapper);

    }



    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    public ExceptionProcessor setClientSendQos(ClientSendQos clientSendQos) {
        this.clientSendQos = clientSendQos;
        return this;
    }
}
