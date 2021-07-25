package com.codedawn.vital.server.processor;

import com.codedawn.vital.server.context.ServerMessageContext;
import com.codedawn.vital.proto.MessageWrapper;

import java.util.concurrent.ExecutorService;

/**
 * @author codedawn
 * @date 2021-07-24 22:45
 */
public interface ServerProcessor {

    void process(ServerMessageContext serverMessageContext, MessageWrapper messageWrapper);


    ExecutorService getExecutor();
}
