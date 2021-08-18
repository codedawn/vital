package com.codedawn.vital.server.processor;

import com.codedawn.vital.server.context.MessageContext;
import com.codedawn.vital.server.proto.MessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author codedawn
 * @date 2021-07-24 22:35
 */
public class ProcessorManager {

    private static Logger log = LoggerFactory.getLogger(ProcessorManager.class);

    private ConcurrentHashMap<String, Processor> processors = new ConcurrentHashMap<>();


    //默认processor
    private Processor defaultProcessor=new Processor() {
        @Override
        public void process(MessageContext messageContext, MessageWrapper messageWrapper) {


        }

        @Override
        public Object preProcess(MessageContext messageContext, MessageWrapper messageWrapper) {

            return null;
        }

        @Override
        public void afterProcess(MessageContext messageContext, MessageWrapper messageWrapper) {

        }

        @Override
        public ExecutorService getExecutor() {
            return null;
        }
    };

    private ExecutorService defaultExecutor;



    public ProcessorManager(ExecutorService defaultExecutor) {
        this.defaultExecutor = defaultExecutor;
    }

    public void registerProcessor(String command, Processor processor) {
        if (processor != null) {
            processors.put(command, processor);
        }
    }

    public Processor getProcessor(String command) {
        Processor processor = processors.get(command);
        if (processor != null) {
            return processor;
        }
        return this.defaultProcessor;
    }

    public ExecutorService getDefaultExecutor() {
        return defaultExecutor;
    }

}
