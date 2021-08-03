package com.codedawn.vital.processor;

import com.codedawn.vital.context.MessageContext;
import com.codedawn.vital.proto.MessageWrapper;

import java.util.concurrent.ExecutorService;

/**
 * @author codedawn
 * @date 2021-07-24 22:45
 */
public interface Processor<T extends MessageContext,E extends MessageWrapper> {

    void process(T messageContext, E messageWrapper);

    void preProcess(T messageContext, E messageWrapper);

    void afterProcess(T messageContext, E messageWrapper);


    ExecutorService getExecutor();
}
