package com.codedawn.vital.server.rpc;

import com.codedawn.vital.server.config.VitalGenericOption;
import com.codedawn.vital.server.proto.Protocol;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * grpc实现的rpcserver
 * @author codedawn
 * @date 2021-09-17 9:42
 */
public class RpcServer {

    private static Logger log = LoggerFactory.getLogger(RpcServer.class);

    private Server server;

    private Protocol protocol;

    public void start()  {
        if(!VitalGenericOption.CLUSTER.value())return;
        try {
            server = ServerBuilder.
                    forPort(VitalGenericOption.CLUSTER_PORT.value())
                    .addService(new RpcSendService().setProtocol(protocol))
                    .build().start();
            log.info("（集群部署）rpc启动成功，正在监听端口：{}",VitalGenericOption.CLUSTER_PORT.value());
            server.awaitTermination();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("集群方式启动失败");
        }
    }

    public void shutdown(){
        if (server!=null){
            server.shutdown();
        }
    }

    public RpcServer setProtocol(Protocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        RpcServer rpcServer = new RpcServer();
        rpcServer.start();
    }
}
