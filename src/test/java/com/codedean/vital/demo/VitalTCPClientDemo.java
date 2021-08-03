package com.codedean.vital.demo;

import com.codedawn.vital.client.TCPClient;
import com.codedawn.vital.client.VitalClient;
import com.codedawn.vital.config.VitalGenericOption;
import com.codedawn.vital.context.DefaultMessageContext;
import com.codedawn.vital.factory.VitalMessageFactory;
import com.codedawn.vital.processor.Processor;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;

import java.util.concurrent.ExecutorService;

/**
 * @author codedawn
 * @date 2021-08-03 17:53
 */
public class VitalTCPClientDemo {
    public static void main(String[] args) {
        VitalClient vitalClient = new VitalClient();
        TCPClient tcpClient = vitalClient.getTcpClient();
        tcpClient.option(VitalGenericOption.ID, "123")
                .registerProcessor(VitalProtobuf.DataType.CommonMessageType.toString(), new Processor<DefaultMessageContext,VitalMessageWrapper>() {


                    @Override
                    public void process(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {
                        System.out.println("收到消息："+messageWrapper.getMessage().getCommonMessage().getMessage());
                    }

                    @Override
                    public void preProcess(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {

                    }

                    @Override
                    public void afterProcess(DefaultMessageContext messageContext, VitalMessageWrapper messageWrapper) {

                    }

                    @Override
                    public ExecutorService getExecutor() {
                        return null;
                    }
                });

        vitalClient.start();
        TCPClient.sender.send(VitalMessageFactory.createCommonMessage("333","你好"));

    }
}
