package com.codedawn.vital.client;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.client.processor.impl.client.AuthSuccessProcessor;
import com.codedawn.vital.client.processor.impl.client.ExceptionProcessor;
import com.codedawn.vital.server.callback.RequestSendCallBack;
import com.codedawn.vital.server.callback.MessageCallBack;
import com.codedawn.vital.server.callback.SendCallBack;
import com.codedawn.vital.server.config.VitalOption;
import com.codedawn.vital.server.processor.Processor;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;

/**
 * @author codedawn
 * @date 2021-07-24 11:02
 */
public class VitalC {

    private TCPClient tcpClient = new TCPClient();

    /**
     * 启动客户端
     */
    public void start(String id, String token, RequestSendCallBack requestSendCallBack) {
        option(ClientVitalGenericOption.TOKEN, token);
        start(id, requestSendCallBack);
    }
    /**
     * 启动客户端
     */
    public void start(String id, RequestSendCallBack requestSendCallBack) {
        option(ClientVitalGenericOption.ID, id);
        tcpClient.start();
        sendAuth(requestSendCallBack);
    }
    /**
     * 关闭客户端
     */
    public void shutdown() {
        tcpClient.shutdown();
    }

    /**
     * 修改框架参数配置{@link ClientVitalGenericOption}
     *
     * @param option
     * @param value
     * @param <T>
     * @return
     */
    public <T> void option(VitalOption<T> option, T value) {
        ClientVitalGenericOption.option(option, value);
    }

    public void setMessageCallBack(MessageCallBack messageCallBack) {
        tcpClient.setMessageCallBack(messageCallBack);
    }

    /**
     * 会覆盖默认设置，使用者的设置优先,command为key，只会存在一个对应value
     */
    public void registerProcessor(String command, Processor processor) {
        tcpClient.registerProcessor(command, processor);
    }

    public void setAuthSuccessProcessor(AuthSuccessProcessor authSuccessProcessor) {
        tcpClient.setAuthSuccessProcessor(authSuccessProcessor);
    }

    public void setExceptionProcessor(ExceptionProcessor exceptionProcessor) {
        tcpClient.setExceptionProcessor(exceptionProcessor);
    }





    /** 适用于qos，responseCallBack回调
     * @param frame
     * @param sendCallBack 消息回调接口
     */
    public void send(VitalPB.Frame frame, SendCallBack sendCallBack){
        tcpClient.sender.send(frame, sendCallBack);
    }

    /** TextMessage适用于qos，responseCallBack回调
     * @param message
     * @param sendCallBack 消息回调接口
     */
    public void send(String toId,String message, SendCallBack sendCallBack){
        tcpClient.sender.send(createTextMessage(getId(),toId,message), sendCallBack);
    }
    /** TextMessage适用于qos，responseCallBack回调
     * @param message
     * @param sendCallBack 消息回调接口
     */
    public void send(String fromId,String toId,String message, SendCallBack sendCallBack){
        tcpClient.sender.send(createTextMessage(fromId,toId,message), sendCallBack);
    }

    public <T>  T createTextMessage(String fromId,String toId, String message){
        return (T) tcpClient.getProtocol().createTextMessage(fromId,toId,message);
    }

    public String getId(){
        return ClientVitalGenericOption.ID.value();
    }

    /**
     * 进行认证，
     */
    public void sendAuth(RequestSendCallBack requestSendCallBack){
        tcpClient.sendAuth(requestSendCallBack);
    }

    /**
     * 注销
     */
    public void sendDisAuth(SendCallBack sendCallBack){
        tcpClient.sendDisAuth(sendCallBack);
    }
    /**
     * 获取tcpClient进行TCP通信的配置
     */
    public TCPClient getTcpClient() {
        return tcpClient;
    }

    public VitalC() {
    }



    public static void main(String[] args) throws InterruptedException {

        VitalC vitalC = new VitalC();
        vitalC.setMessageCallBack(new MessageCallBack() {
            @Override
            public void onMessage(MessageWrapper messageWrapper) {
                VitalPB.TextMessage textMessage = messageWrapper.getMessage();
                System.out.println("收到来自："+messageWrapper.getFromId()+"的消息："+textMessage.getContent());

                vitalC.send("123", "hello", new SendCallBack() {
                    @Override
                    public void onAck(MessageWrapper messageWrapper) {
                        System.out.println("消息已送达"+messageWrapper.getMessage());
                    }

                    @Override
                    public void onException(MessageWrapper exception) {

                    }
                });
            }
        });
        vitalC.start("1234", "213241", new RequestSendCallBack() {
            @Override
            public void onResponse(MessageWrapper response) {
                System.out.println("连接成功");
            }

            @Override
            public void onAck(MessageWrapper messageWrapper) {

            }

            @Override
            public void onException(MessageWrapper exception) {

            }
        });
        vitalC.send("123", "hello", new SendCallBack() {
            @Override
            public void onAck(MessageWrapper messageWrapper) {
                System.out.println("消息已送达"+messageWrapper.getMessage());
            }

            @Override
            public void onException(MessageWrapper exception) {

            }
        });

    }
}
