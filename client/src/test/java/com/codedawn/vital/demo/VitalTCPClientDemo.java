package com.codedawn.vital.demo;


import com.codedawn.vital.client.VitalC;
import com.codedawn.vital.server.callback.MessageCallBack;
import com.codedawn.vital.server.callback.RequestSendCallBack;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;

/**
 * @author codedawn
 * @date 2021-08-03 17:53
 */
public class  VitalTCPClientDemo {
    public static void main(String[] args) throws InterruptedException {
        VitalC vitalC = new VitalC();
//        VitalGenericOption.option(ClientVitalGenericOption.SERVER_TCP_IP, "192.168.1.103");
//        vitalC.setMessageCallBack(new MessageCallBack() {
//            @Override
//            public void onMessage(MessageWrapper messageWrapper) {
//                VitalPB.TextMessage textMessage = messageWrapper.getMessage();
//                System.out.println("收到来自："+messageWrapper.getFromId()+"的消息："+textMessage.getContent());
//            }
//        });

        vitalC.start("7", "213241", new RequestSendCallBack() {
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
//        Thread.sleep(5000);
//        vitalC.send(vitalC.createTextMessage("7", "1", "hello"), new SendCallBack() {
//            @Override
//            public void onAck(MessageWrapper messageWrapper) {
//                System.out.println("消息已送达"+messageWrapper.getMessage());
//            }
//
//            @Override
//            public void onException(MessageWrapper exception) {
//
//            }
//        });

        VitalC vitalC1 = new VitalC();
        vitalC1.setMessageCallBack(new MessageCallBack() {
            @Override
            public void onMessage(MessageWrapper messageWrapper) {
                VitalPB.TextMessage textMessage = messageWrapper.getMessage();
                System.out.println("收到来自："+messageWrapper.getFromId()+"的消息："+textMessage.getContent());
            }
        });
        vitalC1.start("1", "213241", new RequestSendCallBack() {
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
//        for (int i = 1; i <= 20000; i++) {
//            vitalC.send(vitalC.createTextMessage("7", "1", "hello"+i), new SendCallBack() {
//                @Override
//                public void onAck(MessageWrapper messageWrapper) {
//                    System.out.println("消息已送达"+messageWrapper.getMessage());
//                }
//
//                @Override
//                public void onException(MessageWrapper exception) {
//
//                }
//            });
//        }

    }
}

class  VitalTCPClientDemo1 {
    public static void main(String[] args) throws InterruptedException {
        VitalC vitalC = new VitalC();
//        VitalGenericOption.option(ClientVitalGenericOption.SERVER_TCP_IP, "192.168.1.103");
        vitalC.setMessageCallBack(new MessageCallBack() {
            @Override
            public void onMessage(MessageWrapper messageWrapper) {
                VitalPB.TextMessage textMessage = messageWrapper.getMessage();
                System.out.println("收到来自："+messageWrapper.getFromId()+"的消息："+textMessage.getContent());
            }
        });
        vitalC.serverPort(9001);
        vitalC.start("1", "213241", new RequestSendCallBack() {
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
//        Thread.sleep(5000);
//        vitalC.send(vitalC.createTextMessage("1", "7", "hello"), new SendCallBack() {
//            @Override
//            public void onAck(MessageWrapper messageWrapper) {
//                System.out.println("消息已送达"+messageWrapper.getMessage());
//            }
//
//            @Override
//            public void onException(MessageWrapper exception) {
//
//            }
//        });

    }
}

class  VitalTCPClientDemo2 {
    public static void main(String[] args) throws InterruptedException {
        for (int i=1;i<=1000;i++){
            VitalC vitalC = new VitalC();
            vitalC.start(""+i, "", new RequestSendCallBack() {
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
        }

//        Thread.sleep(5000);
//        vitalC.send(vitalC.createTextMessage("1", "7", "hello"), new SendCallBack() {
//            @Override
//            public void onAck(MessageWrapper messageWrapper) {
//                System.out.println("消息已送达"+messageWrapper.getMessage());
//            }
//
//            @Override
//            public void onException(MessageWrapper exception) {
//
//            }
//        });

    }
}

