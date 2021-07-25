package com.codedawn.vital.server.qos;

import com.codedawn.vital.server.proto.ProtocolWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author codedawn
 * @date 2021-07-23 21:40
 */
public class SendQos {

    private static Logger log = LoggerFactory.getLogger(SendQos.class);

    private ConcurrentHashMap<String, ProtocolWrapper> messages = new ConcurrentHashMap<>();

    private static final int MAX_RETRY_COUNT = 3;


    private static final int MAX_DELAY_TIME = 3 * 1000;

    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    private static final int INTERVAL_TIME = 5*1000;


    private static SendQos instance;

    public static SendQos getInstance() {
        if (instance == null) {
            instance = new SendQos();
        }
        return instance;
    }

    private SendQos() {

    }
    private void checkTask() {

        log.info("开始检测是否需要重传 qos");
        ArrayList<ProtocolWrapper> timeoutMessages = new ArrayList<>();

        Iterator<Map.Entry<String, ProtocolWrapper>> iterator = messages.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, ProtocolWrapper> entry = iterator.next();
            ProtocolWrapper protocolWrapper = entry.getValue();

            /**
             * protocol 重传次数到达最大
             */
            if (protocolWrapper.getRetryCount() >= MAX_RETRY_COUNT) {
                timeoutMessages.add(protocolWrapper);
                iterator.remove();
                continue;
            }else {
                Long timeStamp = protocolWrapper.getTimeStamp();
                long toNow = System.currentTimeMillis() - timeStamp;

                /**
                 * ack等待MAX_DELAY_TIME毫秒后还没到达，进行重传
                 */
                if (toNow > MAX_DELAY_TIME) {
                    //todo sendData
                    protocolWrapper.increaseRetryCount();

                } else {
                    log.info("protocol:{} 发送延迟为{}ms，不需要重传",protocolWrapper.getProtocol().getQosId(),toNow);
                }
            }
        }

        log.info("丢失消息{}条",timeoutMessages.size());
    }


    public void start() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    checkTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },0,  INTERVAL_TIME, TimeUnit.MILLISECONDS);

    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        executorService = null;
    }

}
