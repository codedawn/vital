package com.codedawn.vital.demo;


import com.codedawn.vital.client.TCPClient;
import com.codedawn.vital.client.VitalClient;
import com.codedawn.vital.client.connector.Sender;
import com.codedawn.vital.server.callback.MessageCallBack;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import com.codedawn.vital.server.proto.VitalProtobuf;

/**
 * @author codedawn
 * @date 2021-08-03 17:53
 */
public class VitalTCPClientDemo {
    public static void main(String[] args) throws InterruptedException {
        VitalClient vitalClient = new VitalClient();
        TCPClient tcpClient = vitalClient.getTcpClient();
        Sender sender = vitalClient.getTcpClient().getSender();
        vitalClient.setMessageCallBack(new MessageCallBack<VitalMessageWrapper>() {
            @Override
            public void onMessage(VitalMessageWrapper messageWrapper) {
                VitalProtobuf.Protocol.Builder builder=VitalProtobuf.Protocol.newBuilder();
                builder.setMessageType(VitalProtobuf.MessageType.TextMessageType);
                VitalProtobuf.Protocol protocol = builder.build();
                System.out.println(protocol.hasOneof(VitalProtobuf.Protocol.getDescriptor().getOneofs().get(0)));
//                sender.send(ClientVitalMessageFactory.createTextMessage("123",messageWrapper.getProtocol().getTextMessage().getContent()));
//                System.out.println("========");
//                if(messageWrapper.getProtocol().getMessageType()== VitalProtobuf.MessageType.TextMessageType){
//                    VitalProtobuf.TextMessage message = messageWrapper.getMessage();
//                    System.out.println(message);
//                }
                System.out.println(messageWrapper.toString());
            }


        });

        vitalClient.start("123","213241");




        Thread.sleep(2000);
//        sender.send(ClientVitalMessageFactory.createTextMessage("1234", "hello"));

//        while (true) {
//        }


    }
}
