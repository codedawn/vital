package com.codedawn.vital.demo;


import com.codedawn.vital.client.VitalC;
import com.codedawn.vital.server.callback.MessageCallBack;
import com.codedawn.vital.server.callback.ResponseCallBack;
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

        vitalC.start("213","213241");
        Thread.sleep(5000);
        vitalC.send(vitalC.createTextMessage("213", "123", "hello"), new ResponseCallBack() {
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
