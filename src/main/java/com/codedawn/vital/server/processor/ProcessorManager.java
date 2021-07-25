package com.codedawn.vital.server.processor;

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

    private ConcurrentHashMap<String, ServerProcessor> processors = new ConcurrentHashMap<>();


    private ServerProcessor defaultServerProcessor;

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

    public ExecutorService getDefaultExecutor() {
        return defaultExecutor;
    }

}
