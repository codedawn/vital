- [English](README-en.md)
# vital
一个即时通讯脚手架，你可以像插件一样使用，vital只关注消息的传递，消息的持久化和用户的信息将由你自己实现。

# demo ([fim](https://github.com/codedawn/fim.git))

账号1：

<img src="https://edu-codedawn.oss-cn-shenzhen.aliyuncs.com/images/2021/10/07/5D2897ECC894F9F06CE56405C9529D1E.jpg" width="30%">



<img src="https://edu-codedawn.oss-cn-shenzhen.aliyuncs.com/images/2021/10/07/8394A8E54264B45952C0B3380270C947.jpg" width="30%">



<img src="https://edu-codedawn.oss-cn-shenzhen.aliyuncs.com/images/2021/10/07/85919AC4FC93D9BC611F7A77BD0494C2.jpg" width="30%">

<img src="https://edu-codedawn.oss-cn-shenzhen.aliyuncs.com/images/2021/10/07/1C9A090AA3BE4E2C32ABBF64E3940C7D.jpg" width="30%">

<img src="https://edu-codedawn.oss-cn-shenzhen.aliyuncs.com/images/2021/10/07/BED6F1955837914BDC64A1FE4FEA31E3.jpg" width="30%">



账号2：

<img src="https://edu-codedawn.oss-cn-shenzhen.aliyuncs.com/images/2021/10/07/7D8701988E448F5C6D952F140C1C5DE5.jpg" width="30%">



<img src="https://edu-codedawn.oss-cn-shenzhen.aliyuncs.com/images/2021/10/07/082A161047BA887BD17C782077E33EB8.jpg" width="30%">

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
# 关于作者
微信：vx1109609196