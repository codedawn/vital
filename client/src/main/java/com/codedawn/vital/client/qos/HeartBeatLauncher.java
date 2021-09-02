package com.codedawn.vital.client.qos;

import com.codedawn.vital.client.connector.TCPConnect;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.VitalPB;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 心跳发送器
 * @author codedawn
 * @date 2021-07-30 14:52
 */
public class HeartBeatLauncher {
    private static Logger log = LoggerFactory.getLogger(HeartBeatLauncher.class);

    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    private TCPConnect tcpConnect;

    private Protocol<VitalPB.Protocol> protocol;

    public HeartBeatLauncher() {
    }

    public HeartBeatLauncher setTcpConnect(TCPConnect tcpConnect) {
        this.tcpConnect = tcpConnect;
        return this;
    }

    public void heartBeatTask() {
        if (tcpConnect == null) {
            log.error("tcpConnect为null");
            return;
        }
        Channel channel = tcpConnect.getChannel();
        if (channel == null) {
            log.info("发送心跳时channel为null");
            return;
        }
        VitalPB.Protocol heartBeat = protocol.createHeartBeat();
        protocol.send(tcpConnect.getChannel(),heartBeat);
    }


}
