package com.codedawn.vital.server.rpc;

import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.VitalMessageWrapper;
import io.grpc.stub.StreamObserver;

/**
 * @author codedawn
 * @date 2021-09-16 22:31
 */
public class RpcSendService extends VitalRpcServiceGrpc.VitalRpcServiceImplBase{

    private Protocol protocol;
    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void send(VitalRPC.VitalRpcRequest request, StreamObserver<VitalRPC.VitalRpcResponse> responseObserver) {
            responseObserver.onNext(VitalRPC.VitalRpcResponse.newBuilder().build());
            responseObserver.onCompleted();
            protocol.send(request.getId(), new VitalMessageWrapper(request.getFrame()));
    }


    public RpcSendService setProtocol(Protocol protocol) {
        this.protocol = protocol;
        return this;
    }
}
