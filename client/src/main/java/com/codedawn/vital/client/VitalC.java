package com.codedawn.vital.client;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.client.processor.impl.client.AuthSuccessProcessor;
import com.codedawn.vital.client.processor.impl.client.DisAuthSuccessProcessor;
import com.codedawn.vital.client.processor.impl.client.ExceptionProcessor;
import com.codedawn.vital.server.callback.AuthResponseCallBack;
import com.codedawn.vital.server.callback.MessageCallBack;
import com.codedawn.vital.server.callback.ResponseCallBack;
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
    public void start(String id,String token) {
        option(ClientVitalGenericOption.TOKEN, token);
        start(id);
    }
    /**
     * 启动客户端
     */
    public void start(String id) {
        option(ClientVitalGenericOption.ID, id);
        tcpClient.start();
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

    public void setDisAuthSuccessProcessor(DisAuthSuccessProcessor disAuthSuccessProcessor) {
        tcpClient.setDisAuthSuccessProcessor(disAuthSuccessProcessor);
    }

    public void setAuthResponseCallBack(AuthResponseCallBack authResponseCallBack) {
       tcpClient.setAuthResponseCallBack(authResponseCallBack);
    }

    /** 适用于qos，responseCallBack回调
     * @param message
     * @param responseCallBack 消息回调接口
     */
    public void send(VitalPB.Frame message, ResponseCallBack responseCallBack){
        tcpClient.sender.send(message,responseCallBack);
    }

    public <T>  T createTextMessage(String fromId,String toId, String message){
        return (T) tcpClient.getProtocol().createTextMessage(fromId,toId,message);
    }

    /**
     * 进行认证，start的时候会自动调用一次，如果认证失败可以自己调用
     */
    public void auth(){
        tcpClient.auth();
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
            }
        });
        vitalC.start("123","213241");

        Thread.sleep(5000);
        vitalC.send(vitalC.createTextMessage("123", "213", "hello"), new ResponseCallBack() {
            @Override
            public void onAck(MessageWrapper messageWrapper) {
                System.out.println("消息已送达"+messageWrapper.getMessage());
            }

            @Override
            public void exception(MessageWrapper messageWrapper) {

            }
        });


    }
}
