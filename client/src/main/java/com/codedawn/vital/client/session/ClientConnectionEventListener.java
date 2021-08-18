package com.codedawn.vital.client.session;

import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionEventProcessor;
import com.codedawn.vital.server.session.ConnectionEventType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件监听，然后派发到对应eventProcessor
 * @author codedawn
 * @date 2021-07-28 10:52
 */
public class ClientConnectionEventListener {

    public ClientConnectionEventListener() {
    }

    private ConcurrentHashMap<ConnectionEventType, List<ConnectionEventProcessor>> processors = new ConcurrentHashMap<>();




    public void onEvent(String remoteAddress, Connection connection, ConnectionEventType eventType) {
        List<ConnectionEventProcessor> connectionEventProcessors = processors.get(eventType);
        if (connectionEventProcessors != null) {
            for (ConnectionEventProcessor p : connectionEventProcessors) {
                p.onEvent(remoteAddress,connection);
            }
        }
    }



    public void addConnectionEventProcessor(ConnectionEventType eventType, ConnectionEventProcessor connectionEventProcessor) {
        List<ConnectionEventProcessor> eventProcessorList = processors.get(eventType);
        if (eventProcessorList == null) {
            eventProcessorList = new ArrayList<ConnectionEventProcessor>(1);
            processors.put(eventType, eventProcessorList);
        }

        eventProcessorList.add(connectionEventProcessor);
    }
}
