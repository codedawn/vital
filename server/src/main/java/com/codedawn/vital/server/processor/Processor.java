package com.codedawn.vital.server.processor;

import com.codedawn.vital.server.context.MessageContext;
import com.codedawn.vital.server.proto.MessageWrapper;

import java.util.concurrent.ExecutorService;

/**
 *
 * @author codedawn
 * @date 2021-07-24 22:45
 */
public interface Processor<T extends MessageContext,E extends MessageWrapper> {

    void process(T messageContext, E messageWrapper);


    ExecutorService getExecutor();
}
