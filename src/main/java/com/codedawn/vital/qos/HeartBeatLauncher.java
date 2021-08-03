package com.codedawn.vital.qos;

import com.codedawn.vital.connector.TCPConnect;
import com.codedawn.vital.connector.VitalSendHelper;
import com.codedawn.vital.factory.VitalMessageFactory;
import com.codedawn.vital.proto.VitalProtobuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author codedawn
 * @date 2021-07-30 14:52
 */
public class HeartBeatLauncher {
    private static Logger log = LoggerFactory.getLogger(HeartBeatLauncher.class);

    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private static final int HEART_BEAT_INTERVAL_TIME = 3*60*1000;

    private TCPConnect tcpConnect;

    public HeartBeatLauncher(TCPConnect tcpConnect) {
        this.tcpConnect = tcpConnect;
    }

    private void heartBeatTask() {
        Channel channel = tcpConnect.getChannel();
        if (channel == null) {
            log.info("发送心跳时channel为null");
            return;
        }
        VitalProtobuf.Protocol heartBeat = VitalMessageFactory.createHeartBeat();
        VitalSendHelper.send(tcpConnect.getChannel(),heartBeat);
    }

    public void start() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                heartBeatTask();
            }
        },0,HEART_BEAT_INTERVAL_TIME, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        executorService = null;
    }
}
