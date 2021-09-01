package com.codedawn.vital.client;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.client.processor.impl.client.AuthSuccessProcessor;
import com.codedawn.vital.client.processor.impl.client.DisAuthFinishProcessor;
import com.codedawn.vital.client.processor.impl.client.ExceptionProcessor;
import com.codedawn.vital.server.callback.AuthResponseCallBack;
import com.codedawn.vital.server.callback.MessageCallBack;
import com.codedawn.vital.server.processor.Processor;

/**
 * @author codedawn
 * @date 2021-07-24 11:02
 */
public class VitalClient {
    private TCPClient tcpClient = new TCPClient();

    /**
     * 启动客户端
     */
    public void start(String id,String token) {
        ClientVitalGenericOption.option(ClientVitalGenericOption.TOKEN, token);
        start(id);
    }
    /**
     * 启动客户端
     */
    public void start(String id) {
        ClientVitalGenericOption.option(ClientVitalGenericOption.ID, id);
        tcpClient.start();
    }
    /**
     * 关闭客户端
     */
    public void shutdown() {
        tcpClient.shutdown();
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

    public void setDisAuthFinishProcessor(DisAuthFinishProcessor disAuthFinishProcessor) {
        tcpClient.setDisAuthFinishProcessor(disAuthFinishProcessor);
    }

    public void setAuthResponseCallBack(AuthResponseCallBack authResponseCallBack) {
       tcpClient.setAuthResponseCallBack(authResponseCallBack);
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

    public VitalClient() {
    }



    public static void main(String[] args) {
        VitalClient vitalClient = new VitalClient();
    }
}
