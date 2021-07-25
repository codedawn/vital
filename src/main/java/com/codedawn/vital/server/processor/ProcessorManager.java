package com.codedawn.vital.server.processor;

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

    private ConcurrentHashMap<String, ServerProcessor> processors = new ConcurrentHashMap<>();


    private ServerProcessor defaultServerProcessor;

    private ExecutorService defaultExecutor;

    public ProcessorManager() {
    }

    public void registerProcessor(String command,ServerProcessor processor) {
        if (processor != null) {
            processors.put(command, processor);
        }
    }

    public ServerProcessor getProcessor(String command) {
        ServerProcessor serverProcessor = processors.get(command);
        if (serverProcessor != null) {
            return serverProcessor;
        }
        return this.defaultServerProcessor;
    }

}
