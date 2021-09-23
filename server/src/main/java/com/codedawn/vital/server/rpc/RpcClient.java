package com.codedawn.vital.server.rpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * grpc实现的rpcclient
 * @author codedawn
 * @date 2021-09-17 10:09
 */
public class RpcClient {

    /**
     * channel 复用问题
     * https://stackoverflow.com/questions/63749113/grpc-call-channel-connection-and-http-2-lifecycle
     */
    private ConcurrentHashMap<String, ManagedChannel> channelMap = new ConcurrentHashMap<>();

    public VitalRpcServiceGrpc.VitalRpcServiceFutureStub getStub(String address) {
        ManagedChannel managedChannel = channelMap.get(address);
        if (managedChannel == null||managedChannel.isShutdown()) {
            managedChannel = createChannel(address);
        }
        return VitalRpcServiceGrpc.newFutureStub(managedChannel);
    }

    private ManagedChannel createChannel(String address) {
        String[] strings = address.split(":");
        if (strings.length < 1) throw new RuntimeException("地址不正确");
        ManagedChannel newManagedChannel = ManagedChannelBuilder.forAddress(strings[0], Integer.parseInt(strings[1]))
                .usePlaintext()
//                .keepAliveTime(2, TimeUnit.SECONDS)
//                .keepAliveTimeout(5,TimeUnit.SECONDS)
                .build();
        ManagedChannel oldChannel = channelMap.put(address, newManagedChannel);
        if (oldChannel != null) {
            oldChannel.shutdown();
        }
        return newManagedChannel;
    }



}
