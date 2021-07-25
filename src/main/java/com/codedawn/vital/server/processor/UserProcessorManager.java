package com.codedawn.vital.server.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author codedawn
 * @date 2021-07-24 23:14
 */
public class UserProcessorManager {
    private static Logger log = LoggerFactory.getLogger(UserProcessorManager.class);

    private ConcurrentHashMap<String, UserProcessor> userProcessors;

    private ExecutorService defaultExecutor;

    public UserProcessorManager() {
    }


}
