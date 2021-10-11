- [中文](README.md)
# vital
An instant messaging scaffolding that you can use like a plugin, Vital only cares about message delivery, message persistence and user information will be implemented by you.

# involving technology
- netty
- protobuf
- grpc

# features
- heartbeat detection
- FRTD
- qos
- cluster

# quick start
server
```java
VitalS vitalS = new VitalS()
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
        vitalC.send("1", "hello", new SendCallBack() {
             @Override
            public void onAck(MessageWrapper messageWrapper) {
                 System.out.println("消息已送达"+messageWrapper.getMessage());
            }

             @Override
            public void onException(MessageWrapper exception) {
    
            }
        });
```
# about
wx：vx1109609196