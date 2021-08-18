package com.codedawn.vital.processor;

import com.codedawn.vital.context.MessageContext;
import com.codedawn.vital.proto.MessageWrapper;

import java.util.concurrent.ExecutorService;

/**
 * preProcess和AfterProcess，没有强制调用，只是为了方便向外提供接口
 * @author codedawn
 * @date 2021-07-24 22:45
 */
public interface Processor<T extends MessageContext,E extends MessageWrapper> {

    void process(T messageContext, E messageWrapper);

    Object preProcess(T messageContext, E messageWrapper);

    void afterProcess(T messageContext, E messageWrapper);


    ExecutorService getExecutor();
}
