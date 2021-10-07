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
        vitalC.serverIp("127.0.0.1").serverPort(8000);
        vitalC.setMessageCallBack(new MessageCallBack() {
            @Override
            public void onMessage(MessageWrapper messageWrapper) {
                VitalPB.TextMessage textMessage = messageWrapper.getMessage();
                System.out.println("收到来自："+messageWrapper.getFromId()+"的消息："+textMessage.getContent());
            }
        });

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
        Thread.sleep(3000);
        vitalC.sendDisAuth(new SendCallBack() {
            @Override
            public void onAck(MessageWrapper messageWrapper) {
                System.out.println("注销成功");
            }

            @Override
            public void onException(MessageWrapper exception) {

            }
        });
        Thread.sleep(2000);
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
    }
}

class  VitalTCPClientDemo1 {
    public static void main(String[] args) throws InterruptedException {
        VitalC vitalC = new VitalC();
        vitalC.setMessageCallBack(new MessageCallBack() {
            @Override
            public void onMessage(MessageWrapper messageWrapper) {
                VitalPB.TextMessage textMessage = messageWrapper.getMessage();
                System.out.println("收到来自："+messageWrapper.getFromId()+"的消息："+textMessage.getContent());
            }
        });
        vitalC.start("2", "213241", new RequestSendCallBack() {
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
        Thread.sleep(5000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 1; i <= 500000; i++) {
                    vitalC.send("1", i + "", new SendCallBack() {
                        @Override
                        public void onAck(MessageWrapper messageWrapper) {
                            System.out.println("消息已送达" + messageWrapper.getMessage());
                        }

                        @Override
                        public void onException(MessageWrapper exception) {

                        }
                    });
                }

            }
        }).start();

    }
}

class  VitalTCPClientDemo2 {
    public static void main(String[] args) throws InterruptedException {
        VitalC vitalC = new VitalC();
        vitalC.setMessageCallBack(new MessageCallBack() {
            @Override
            public void onMessage(MessageWrapper messageWrapper) {
                VitalPB.TextMessage textMessage = messageWrapper.getMessage();
                System.out.println("收到来自："+messageWrapper.getFromId()+"的消息："+textMessage.getContent());
            }
        });
        vitalC.start("3", "213241", new RequestSendCallBack() {
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
        Thread.sleep(5000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 1; i <= 500000; i++) {
                    vitalC.send("1", i + "", new SendCallBack() {
                        @Override
                        public void onAck(MessageWrapper messageWrapper) {
                            System.out.println("消息已送达" + messageWrapper.getMessage());
                        }

                        @Override
                        public void onException(MessageWrapper exception) {

                        }
                    });
                }

            }
        }).start();

    }
}
