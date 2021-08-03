package com.codedawn.vital.processor.impl.client;

import com.codedawn.vital.connector.Sender;
import com.codedawn.vital.context.DefaultMessageContext;
import com.codedawn.vital.processor.Processor;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
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

    public ExceptionProcessor(ExecutorService executor, Sender sender) {
        this.executor = executor;
        this.sender = sender;
    }

    public ExceptionProcessor(Sender sender) {
        this.sender = sender;
    }

    @Override
    public void process(DefaultMessageContext defaultMessageContext, VitalMessageWrapper vitalMessageWrapper) {
        preProcess(defaultMessageContext,vitalMessageWrapper);
        VitalProtobuf.ExceptionMessage exceptionMessage = vitalMessageWrapper.getMessage().getExceptionMessage();
        sender.invokeExceptionCallback(vitalMessageWrapper);
        afterProcess(defaultMessageContext,vitalMessageWrapper);
    }

    @Override
    public void preProcess(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {

    }

    @Override
    public void afterProcess(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {

    }

    @Override
    public ExecutorService getExecutor() {
        return null;
    }
}
