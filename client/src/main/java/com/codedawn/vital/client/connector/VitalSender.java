package com.codedawn.vital.client.connector;

import com.codedawn.vital.server.callback.RequestSendCallBack;
import com.codedawn.vital.server.callback.SendCallBack;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author codedawn
 * @date 2021-07-25 8:53
 */
public class VitalSender implements Sender<VitalPB.Frame,VitalMessageWrapper> {


    private static Logger log = LoggerFactory.getLogger(VitalSender.class);


    private TCPConnect tcpConnect;

    private Protocol<VitalPB.Frame> protocol;
    /**
     * 消息回调，ack到达时调用
     */
    private ConcurrentHashMap<String, SendCallBack> messageCallBackMap = new ConcurrentHashMap<>();



    public VitalSender() {

    }


    /**
     * 如果需要回调应该使用{@link VitalSender#send(VitalPB.Frame, SendCallBack)}
     * @param frame
     */
    @Override
    public void send(VitalPB.Frame frame) {
        protocol.send(tcpConnect.getChannel(),frame);
    }

    /**
     * 该方法适用于开启qos的消息
     * @param messageWrapper
     */
    @Override
    public void send(VitalMessageWrapper messageWrapper) {
        protocol.send(tcpConnect.getChannel(),messageWrapper);
    }

    /** 适用于qos，responseCallBack回调
     * @param frame
     * @param sendCallBack 消息回调接口
     */
    @Override
    public void send(VitalPB.Frame frame, SendCallBack sendCallBack) {
        protocol.send(tcpConnect.getChannel(),frame, sendCallBack);
    }

    @Override
    public void send(Channel channel, VitalPB.Frame frame, RequestSendCallBack requestSendCallBack){
        protocol.send(channel,frame,requestSendCallBack);
    }


    public VitalSender setTcpConnect(TCPConnect tcpConnect) {
        this.tcpConnect = tcpConnect;
        return this;
    }

    public VitalSender setProtocol(Protocol<VitalPB.Frame> protocol) {
        this.protocol = protocol;
        return this;
    }
}
