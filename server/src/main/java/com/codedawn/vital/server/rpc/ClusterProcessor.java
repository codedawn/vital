package com.codedawn.vital.server.rpc;

import com.codedawn.vital.server.config.VitalGenericOption;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * 集群处理器，重写onAddress方法，返回id所在的服务器地址，格式为 ip:port
 *
 * @author codedawn
 * @date 2021-09-17 15:11
 */
public class ClusterProcessor {

    private static Logger log = LoggerFactory.getLogger(ClusterProcessor.class);

    private RpcClient rpcClient;

    public void send(String id, MessageWrapper messageWrapper) {
        VitalRpcServiceGrpc.VitalRpcServiceFutureStub stub = rpcClient.getStub(onAddress(id));

        if (stub == null) throw new RuntimeException("获取其他节点失败");
        ListenableFuture<VitalRPC.VitalRpcResponse> send = stub.send(VitalRPC.VitalRpcRequest.newBuilder().setId(id).setFrame((VitalPB.Frame) messageWrapper.getFrame()).build());
        try {
            send.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            log.warn("集群转发失败");
            e.printStackTrace();
        }
    }


    /**
     * 实现该方法，返回id所在的节点，格式为 ip:port
     * @param id
     * @return
     */
    protected String onAddress(String id) {
//        return "127.0.0.1:9091";
        if(VitalGenericOption.SERVER_TCP_PORT.value()==8000){
            return "127.0.0.1:9091";
        }
        else {
            return "127.0.0.1:9099";
        }
    }

    public ClusterProcessor setRpcClient(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        return this;
    }
}
