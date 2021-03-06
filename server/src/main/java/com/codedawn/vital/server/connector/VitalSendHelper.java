package com.codedawn.vital.server.connector;


import com.codedawn.vital.server.callback.RequestSendCallBack;
import com.codedawn.vital.server.callback.SendCallBack;
import com.codedawn.vital.server.config.VitalGenericOption;
import com.codedawn.vital.server.logic.OfflineMessageLogic;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;
import com.codedawn.vital.server.qos.SendQos;
import com.codedawn.vital.server.rpc.ClusterProcessor;
import com.codedawn.vital.server.session.Connection;
import com.codedawn.vital.server.session.ConnectionManage;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-29 10:18
 */
public class VitalSendHelper {


    private ConnectionManage connectionManage;

    private SendQos sendQos;

    private ClusterProcessor clusterProcessor;

    private OfflineMessageLogic offlineMessageLogic=new OfflineMessageLogic();

    public VitalSendHelper() {

    }


    private static Logger log = LoggerFactory.getLogger(VitalSendHelper.class);

    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param frame
     */
    public  void send(Channel channel, VitalPB.Frame frame) {
        if(frame.getHeader().getIsQos()){
            VitalMessageWrapper vitalMessageWrapper = new VitalMessageWrapper(frame);
            send(channel,vitalMessageWrapper);
        }else {
            send0(channel,frame);
        }

    }

    /**
     * 该方法适用于开启qos的消息,带回调
     * @param channel
     * @param frame
     * @param sendCallBack
     */
    public  void send(Channel channel, VitalPB.Frame frame, SendCallBack sendCallBack) {
        sendQos.putCallBackIfAbsent(frame.getHeader().getSeq(), sendCallBack);
        send(channel,frame);
    }

    /**
     * 该方法适用于开启qos的消息，带操作回调
     * @param channel
     * @param frame
     * @param requestSendCallBack
     */
    public  void send(Channel channel, VitalPB.Frame frame, RequestSendCallBack requestSendCallBack) {
        sendQos.putCallBackIfAbsent(frame.getHeader().getSeq(), requestSendCallBack);
        send(channel,frame);
    }

    /**
     * 该方法适用于开启qos的消息
     * @param channel
     * @param messageWrapper
     */
    public  void send(Channel channel, MessageWrapper messageWrapper) {

        if (messageWrapper.getIsQos()) {
            sendQos.addMessageIfAbsent(messageWrapper.getSeq(),messageWrapper);
        }else {
//            log.info("设置了MessageCallBack，但是没有开启qos，所以永远不会调用MessageCallBack");
        }
        send0(channel,messageWrapper.getFrame());
    }

    /**
     * 直接调用该方法，默认不启用sendQos，ack消息发送直接使用该方法，ack消息不需要重发
     * @param channel
     * @param frame
     */
    public  void send0(Channel channel, VitalPB.Frame frame) {
        if (channel == null) {
            log.info("VitalSendHelper#send发送消息时，channel为null");
            return;
        }

        log.debug("发送了{},seq:{}",frame.toString(),frame.getHeader().getSeq());
        channel.writeAndFlush(frame);
    }

    /**
     * 该方法适用于开启qos的消息
     * @param id
     * @param messageWrapper
     */
    public void send(String id, MessageWrapper messageWrapper){
        if(connectionManage==null){
            log.warn("send中connectionManage为null，需要发送的消息将无法发送");
            return;
        }
        Connection connection = connectionManage.get(id);
        if(connection!=null){
            send(connection.getChannel(),messageWrapper);
        }else {
            if(VitalGenericOption.CLUSTER.value()){
                //消息qos
                sendQos.addMessageIfAbsent(messageWrapper.getSeq(),messageWrapper);
                if(clusterProcessor.send(id, messageWrapper)){
                    sendQos.removeMessage(messageWrapper.getSeq());
                }else {
                    sendQos.removeMessage(messageWrapper.getSeq());
                    onOffline(id,messageWrapper);
//                    log.warn("send中seq:{}消息集群转发失败，目标用户id:{}不在线或者是其他异常",messageWrapper.getSeq(),id);
                }
            }else {
                onOffline(id,messageWrapper);
//                log.warn("send中id:{}对应的connection为null，说明该id不在线，需要发送的消息将无法发送",id);
            }
        }
    }

    private void onOffline(String id,MessageWrapper messageWrapper){
        log.info("send中seq:{}消息无法投递到id:{}，该消息将触发OfflineMessageLogic",messageWrapper.getSeq(),id);
        offlineMessageLogic.OnOffline(messageWrapper);
    }
    public VitalSendHelper setConnectionManage(ConnectionManage connectionManage) {
        this.connectionManage = connectionManage;
        return this;
    }

    public VitalSendHelper setSendQos(SendQos sendQos) {
        this.sendQos = sendQos;
        return this;
    }

    public VitalSendHelper setClusterProcessor(ClusterProcessor clusterProcessor) {
        this.clusterProcessor = clusterProcessor;
        return this;
    }

    public VitalSendHelper setOfflineMessageLogic(OfflineMessageLogic offlineMessageLogic) {
        this.offlineMessageLogic = offlineMessageLogic;
        return this;
    }
}
