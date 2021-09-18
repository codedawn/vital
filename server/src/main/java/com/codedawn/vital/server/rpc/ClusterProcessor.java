package com.codedawn.vital.server.rpc;

import com.codedawn.vital.server.config.VitalGenericOption;
import com.codedawn.vital.server.proto.MessageWrapper;
import com.codedawn.vital.server.proto.VitalPB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        VitalRpcServiceGrpc.VitalRpcServiceBlockingStub stub = rpcClient.getStub(onAddress(id));

        if (stub == null) throw new RuntimeException("获取其他节点失败");
        stub.send(VitalRPC.VitalRpcRequest.newBuilder().setId(id).setFrame((VitalPB.Frame) messageWrapper.getFrame()).build());

    }

    protected String onAddress(String id) {
        if(VitalGenericOption.SERVER_TCP_PORT.value()==8000){
            return "127.0.0.1:9091";
        }
        else {
            return "127.0.0.1:9090";
        }
    }

    public ClusterProcessor setRpcClient(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        return this;
    }
}
