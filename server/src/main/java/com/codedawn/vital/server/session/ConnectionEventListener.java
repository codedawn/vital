package com.codedawn.vital.server.session;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 事件监听，然后派发到对应eventProcessor
 * @author codedawn
 * @date 2021-07-28 10:52
 */
public class ConnectionEventListener {

    public ConnectionEventListener() {
    }

    private ConcurrentHashMap<ConnectionEventType, List<ConnectionEventProcessor>> processors = new ConcurrentHashMap<>();

    protected ExecutorService executor=new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100000),
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
