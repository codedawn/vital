package com.codedawn.vital.processor;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author codedawn
 * @date 2021-07-24 22:35
 */
public class ProcessorManager {

    private static Logger log = LoggerFactory.getLogger(ProcessorManager.class);

    private ConcurrentHashMap<String, Processor> processors = new ConcurrentHashMap<>();


    private Processor defaultProcessor;

    private ExecutorService defaultExecutor;

    private int                                                  minPoolSize    =20;

    private int                                                  maxPoolSize    = 200;

    private int                                                  queueSize      = 500;

    private long                                                 keepAliveTime  =60;

    public ProcessorManager() {
        //todo 后序完善
        this.defaultExecutor = new ThreadPoolExecutor(minPoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize), new DefaultThreadFactory(
                "vital-default-executor", true));
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
