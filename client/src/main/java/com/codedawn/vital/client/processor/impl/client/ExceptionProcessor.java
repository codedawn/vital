package com.codedawn.vital.client.processor.impl.client;

import com.codedawn.vital.client.connector.Sender;
import com.codedawn.vital.server.context.DefaultMessageContext;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * 收到异常消息使用该处理器，用于客户端
 * @author codedawn
 * @date 2021-07-29 16:04
 */
public class ExceptionProcessor implements Processor<DefaultMessageContext,VitalMessageWrapper> {

    private static Logger log = LoggerFactory.getLogger(ExceptionProcessor.class);

    private ExecutorService executor;

    private Sender sender;

    public ExceptionProcessor() {
    }

    @Override
    public void process(DefaultMessageContext defaultMessageContext, VitalMessageWrapper vitalMessageWrapper) {
        //触发消息回调
        sender.invokeExceptionCallback(vitalMessageWrapper);

    }



    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    public ExceptionProcessor setSender(Sender sender) {
        this.sender = sender;
        return this;
    }
}
