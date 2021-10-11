package com.codedawn.vital.server.rpc;

import com.codedawn.vital.server.logic.ClusterLogic;
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

    private ClusterLogic clusterLogic = new ClusterLogic();

    /**
     * 发生成功返回true，否则返回false
     * @param id
     * @param messageWrapper
     * @return
     */
    public boolean send(String id, MessageWrapper messageWrapper) {
        String address = clusterLogic.onAddress(id);
        if(address==null)return false;
        VitalRpcServiceGrpc.VitalRpcServiceFutureStub stub = rpcClient.getStub(address);

        if (stub == null) throw new RuntimeException("获取其他节点失败");
        ListenableFuture<VitalRPC.VitalRpcResponse> send = stub.send(VitalRPC.VitalRpcRequest.newBuilder().setId(id).setFrame((VitalPB.Frame) messageWrapper.getFrame()).build());
        try {
            send.get();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            log.warn("集群转发失败");
            e.printStackTrace();
            return false;
        }
    }


    public ClusterProcessor setRpcClient(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        return this;
    }

    public ClusterProcessor setClusterLogic(ClusterLogic clusterLogic) {
        this.clusterLogic = clusterLogic;
        return this;
    }
}
