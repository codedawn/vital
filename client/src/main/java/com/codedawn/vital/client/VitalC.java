package com.codedawn.vital.client;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.server.callback.MessageCallBack;
import com.codedawn.vital.server.callback.RequestSendCallBack;
import com.codedawn.vital.server.callback.SendCallBack;
import com.codedawn.vital.server.config.VitalGenericOption;
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
     *
     * @param ip
     * @return
     */
    public VitalC serverIp(String ip){
        option(ClientVitalGenericOption.SERVER_TCP_IP,ip);
        return this;
    }

    public VitalC serverPort(int port){
        option(ClientVitalGenericOption.SERVER_TCP_PORT,port);
        return this;
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

//    /**
//     * 会覆盖默认设置，使用者的设置优先,command为key，只会存在一个对应value
//     */
//    public void registerProcessor(String command, Processor processor) {
//        tcpClient.registerProcessor(command, processor);
//    }

    /**
     * UserProcessor默认没有设置processor
     *
     * @param command
     * @param processor
     */
    public void registerUserProcessor(String command, Processor processor) {
        tcpClient.registerUserProcessor(command, processor);
    }

    /**
     * 适用于qos，responseCallBack回调
     *
     * @param frame
     * @param sendCallBack 消息回调接口
     */
    public void send(VitalPB.Frame frame, SendCallBack sendCallBack) {
        tcpClient.sender.send(frame, sendCallBack);
    }

    /**
     * TextMessage适用于qos，responseCallBack回调
     *
     * @param message
     * @param sendCallBack 消息回调接口
     */
    public void send(String toId, String message, SendCallBack sendCallBack) {
        tcpClient.sender.send(createTextMessage(getId(), toId, message), sendCallBack);
    }

    /**
     * TextMessage适用于qos，responseCallBack回调
     *
     * @param message
     * @param sendCallBack 消息回调接口
     */
    public void send(String fromId, String toId, String message, SendCallBack sendCallBack) {
        tcpClient.sender.send(createTextMessage(fromId, toId, message), sendCallBack);
    }

    /**
     * TextMessage适用于qos，responseCallBack回调
     *
     * @param message
     * @param sendCallBack 消息回调接口
     */
    public void sendGroup(String toId, String message, SendCallBack sendCallBack) {
        tcpClient.sender.send(createGroupTextMessage(getId(), toId, message), sendCallBack);
    }

    /**
     * TextMessage适用于qos，responseCallBack回调
     *
     * @param message
     * @param sendCallBack 消息回调接口
     */
    public void sendGroup(String fromId, String toId, String message, SendCallBack sendCallBack) {
        tcpClient.sender.send(createGroupTextMessage(fromId, toId, message), sendCallBack);
    }

    /**
     * ImageMessage适用于qos，responseCallBack回调
     *
     * @param url
     * @param sendCallBack 消息回调接口
     */
    public void sendImage(String fromId, String toId, String url, SendCallBack sendCallBack) {
        tcpClient.sender.send(createImageMessage(fromId, toId, url), sendCallBack);
    }

    /**
     * ImageMessage适用于qos，responseCallBack回调
     *
     * @param url
     * @param sendCallBack 消息回调接口
     */
    public void sendGroupImage(String fromId, String toId, String url, SendCallBack sendCallBack) {
        tcpClient.sender.send(createGroupImageMessage(fromId, toId, url), sendCallBack);
    }

    public <T> T createTextMessage(String fromId, String toId, String message) {
        return (T) tcpClient.getProtocol().createTextMessage(fromId, toId, message);
    }

    public <T> T createGroupTextMessage(String fromId, String toId, String message) {
        return (T) tcpClient.getProtocol().createGroupTextMessage(fromId, toId, message);
    }

    public <T> T createImageMessage(String fromId, String toId, String url) {
        return (T) tcpClient.getProtocol().createImageMessage(fromId, toId, url);
    }

    public <T> T createGroupImageMessage(String fromId, String toId, String url) {
        return (T) tcpClient.getProtocol().createGroupImageMessage(fromId, toId, url);
    }


    public String getId() {
        return ClientVitalGenericOption.ID.value();
    }

    /**
     * 进行认证，
     */
    public void sendAuth(RequestSendCallBack requestSendCallBack) {
        tcpClient.sendAuth(requestSendCallBack);
    }

    /**
     * 注销
     */
    public void sendDisAuth(SendCallBack sendCallBack) {
        tcpClient.sendDisAuth(sendCallBack);
    }

    /**
     * 获取tcpClient进行TCP通信的配置
     */
    public TCPClient getTcpClient() {
        return tcpClient;
    }

    public VitalC() {
        VitalGenericOption.option(VitalGenericOption.SERVER_SIDE, false);
    }


    public static void main(String[] args) throws InterruptedException {

        VitalC vitalC = new VitalC();
//        vitalC.setMessageCallBack(new MessageCallBack() {
//            @Override
//            public void onMessage(MessageWrapper messageWrapper) {
//                Object message = messageWrapper.getMessage();
//                if (!(message instanceof VitalPB.TextMessage)) {
//                    return;
//                }
//                VitalPB.TextMessage textMessage = (VitalPB.TextMessage) message;
//                System.out.println("收到来自：" + messageWrapper.getFromId() + "的消息：" + textMessage.getContent());
//
//                vitalC.send("1", "hello", new SendCallBack() {
//                    @Override
//                    public void onAck(MessageWrapper messageWrapper) {
//                        System.out.println("消息已送达" + messageWrapper.getMessage());
//                    }
//
//                    @Override
//                    public void onException(MessageWrapper exception) {
//
//                    }
//                });
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
        vitalC.send("1", "hello", new SendCallBack() {
            @Override
            public void onAck(MessageWrapper messageWrapper) {
                System.out.println("消息已送达"+messageWrapper.getMessage());
            }

            @Override
            public void onException(MessageWrapper exception) {

            }
        });

        Thread.sleep(5000);
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 1; i <= 100000; i++) {
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
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                for (int i=200001;i<=500000;i++){
//                    vitalC.send("123", i+"", new SendCallBack() {
//                        @Override
//                        public void onAck(MessageWrapper messageWrapper) {
//                            System.out.println("消息已送达"+messageWrapper.getMessage());
//                        }
//
//                        @Override
//                        public void onException(MessageWrapper exception) {
//
//                        }
//                    });
//                }
//
//            }
//        }).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                for (int i=200001;i<=300000;i++){
//                    vitalC.send("123", i+"", new SendCallBack() {
//                        @Override
//                        public void onAck(MessageWrapper messageWrapper) {
//                            System.out.println("消息已送达"+messageWrapper.getMessage());
//                        }
//
//                        @Override
//                        public void onException(MessageWrapper exception) {
//
//                        }
//                    });
//                }
//
//            }
//        }).start();


    }
}
