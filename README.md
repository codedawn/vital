# vital
一个即时通讯脚手架，你可以像插件一样使用，vital只关注消息的传递，消息的持久化和用户的信息将由你自己实现。

# 涉及技术
- netty
- protobuf
- grpc

# 特性
- 心跳检测
- 断线重连
- qos
- 集群

# 快速开始
server
```java
 VitalS vitalS = new VitalS();
        vitalS.cluster(true)
                .clusterPort(9091)
                .port(9001);
        vitalS.start();
```

client
```java
VitalC vitalC = new VitalC();
        vitalC.serverIp("127.0.0.1").serverPort(9001);
        vitalC.setMessageCallBack(new MessageCallBack() {
            @Override
            public void onMessage(MessageWrapper messageWrapper) {
                VitalPB.TextMessage textMessage = messageWrapper.getMessage();
                System.out.println("收到来自："+messageWrapper.getFromId()+"的消息："+textMessage.getContent());
            }
        });

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
        Thread.sleep(5000);
        vitalC.send(vitalC.createTextMessage("7", "1", "hello"), new SendCallBack() {
@Override
public void onAck(MessageWrapper messageWrapper) {
        System.out.println("消息已送达"+messageWrapper.getMessage());
        }

@Override
public void onException(MessageWrapper exception) {

        }
        });
```
#关于作者
微信：vx1109609196