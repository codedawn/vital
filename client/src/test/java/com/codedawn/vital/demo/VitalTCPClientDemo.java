package com.codedawn.vital.demo;


import com.codedawn.vital.client.VitalC;
import com.codedawn.vital.server.callback.MessageCallBack;
import com.codedawn.vital.server.callback.RequestSendCallBack;
import com.codedawn.vital.server.callback.SendCallBack;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;

/**
 * @author codedawn
 * @date 2021-08-03 17:53
 */
public class  VitalTCPClientDemo {
    public static void main(String[] args) throws InterruptedException {
        VitalC vitalC = new VitalC();

        vitalC.setMessageCallBack(new MessageCallBack() {
            @Override
            public void onMessage(MessageWrapper messageWrapper) {
                VitalPB.TextMessage textMessage = messageWrapper.getMessage();
                System.out.println("收到来自："+messageWrapper.getFromId()+"的消息："+textMessage.getContent());
            }
        });

        vitalC.start("1234", "213241", new RequestSendCallBack() {
            @Override
            public void onResponse(MessageWrapper response) {

            }

            @Override
            public void onAck(MessageWrapper messageWrapper) {

            }

            @Override
            public void onException(MessageWrapper exception) {

            }
        });
        Thread.sleep(5000);
        vitalC.send(vitalC.createTextMessage("213", "1234", "hello"), new SendCallBack() {
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
