package com.codedawn.vital.client.processor.impl.client;

import com.codedawn.vital.client.TCPClient;
import com.codedawn.vital.server.context.DefaultMessageContext;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author codedawn
 * @date 2021-09-06 16:19
 */
public class KickoutProcessor implements Processor<DefaultMessageContext, VitalMessageWrapper> {

    private static Logger log = LoggerFactory.getLogger(KickoutProcessor.class);

    private TCPClient tcpClient;

    @Override
    public void process(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {
        log.warn("当前用户被服务器提出");
        tcpClient.clear();
    }

    @Override
    public ExecutorService getExecutor() {
        return null;
    }

    public KickoutProcessor setTcpClient(TCPClient tcpClient) {
        this.tcpClient = tcpClient;
        return this;
    }
}
