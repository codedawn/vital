package com.codedawn.vital.session;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author codedawn
 * @date 2021-07-28 10:52
 */
public class ConnectionEventListener {

    public ConnectionEventListener() {
    }

    private ConcurrentHashMap<ConnectionEventType, List<ConnectionEventProcessor>> processors = new ConcurrentHashMap<>();

    private ExecutorService executor=new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(10000),
            new DefaultThreadFactory("Vital-conn-event-executor", true));;


    public void onEvent(String remoteAddress, Connection connection, ConnectionEventType eventType) {
        List<ConnectionEventProcessor> connectionEventProcessors = processors.get(eventType);
        if (connectionEventProcessors != null) {
            for (ConnectionEventProcessor p : connectionEventProcessors) {
                p.onEvent(remoteAddress,connection);
            }
        }
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void addConnectionEventProcessor(ConnectionEventType eventType,ConnectionEventProcessor connectionEventProcessor) {
        List<ConnectionEventProcessor> eventProcessorList = processors.get(eventType);
        if (eventProcessorList == null) {
            eventProcessorList = new ArrayList<ConnectionEventProcessor>(1);
            processors.put(eventType, eventProcessorList);
        }

        eventProcessorList.add(connectionEventProcessor);
    }
}
