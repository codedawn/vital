package com.codedawn.vital.server.rpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.40.1)",
    comments = "Source: rpc.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class VitalRpcServiceGrpc {

  private VitalRpcServiceGrpc() {}

  public static final String SERVICE_NAME = "VitalRpcService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.codedawn.vital.server.rpc.VitalRPC.VitalRpcRequest,
      com.codedawn.vital.server.rpc.VitalRPC.VitalRpcResponse> getSendMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "send",
      requestType = com.codedawn.vital.server.rpc.VitalRPC.VitalRpcRequest.class,
      responseType = com.codedawn.vital.server.rpc.VitalRPC.VitalRpcResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.codedawn.vital.server.rpc.VitalRPC.VitalRpcRequest,
      com.codedawn.vital.server.rpc.VitalRPC.VitalRpcResponse> getSendMethod() {
    io.grpc.MethodDescriptor<com.codedawn.vital.server.rpc.VitalRPC.VitalRpcRequest, com.codedawn.vital.server.rpc.VitalRPC.VitalRpcResponse> getSendMethod;
    if ((getSendMethod = VitalRpcServiceGrpc.getSendMethod) == null) {
      synchronized (VitalRpcServiceGrpc.class) {
        if ((getSendMethod = VitalRpcServiceGrpc.getSendMethod) == null) {
          VitalRpcServiceGrpc.getSendMethod = getSendMethod =
              io.grpc.MethodDescriptor.<com.codedawn.vital.server.rpc.VitalRPC.VitalRpcRequest, com.codedawn.vital.server.rpc.VitalRPC.VitalRpcResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "send"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codedawn.vital.server.rpc.VitalRPC.VitalRpcRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.codedawn.vital.server.rpc.VitalRPC.VitalRpcResponse.getDefaultInstance()))
              .setSchemaDescriptor(new VitalRpcServiceMethodDescriptorSupplier("send"))
              .build();
        }
      }
    }
    return getSendMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static VitalRpcServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VitalRpcServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VitalRpcServiceStub>() {
        @java.lang.Override
        public VitalRpcServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VitalRpcServiceStub(channel, callOptions);
        }
      };
    return VitalRpcServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static VitalRpcServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VitalRpcServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VitalRpcServiceBlockingStub>() {
        @java.lang.Override
        public VitalRpcServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VitalRpcServiceBlockingStub(channel, callOptions);
        }
      };
    return VitalRpcServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static VitalRpcServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<VitalRpcServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<VitalRpcServiceFutureStub>() {
        @java.lang.Override
        public VitalRpcServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new VitalRpcServiceFutureStub(channel, callOptions);
        }
      };
    return VitalRpcServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class VitalRpcServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void send(com.codedawn.vital.server.rpc.VitalRPC.VitalRpcRequest request,
        io.grpc.stub.StreamObserver<com.codedawn.vital.server.rpc.VitalRPC.VitalRpcResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.codedawn.vital.server.rpc.VitalRPC.VitalRpcRequest,
                com.codedawn.vital.server.rpc.VitalRPC.VitalRpcResponse>(
                  this, METHODID_SEND)))
          .build();
    }
  }

  /**
   */
  public static final class VitalRpcServiceStub extends io.grpc.stub.AbstractAsyncStub<VitalRpcServiceStub> {
    private VitalRpcServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VitalRpcServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VitalRpcServiceStub(channel, callOptions);
    }

    /**
     */
    public void send(com.codedawn.vital.server.rpc.VitalRPC.VitalRpcRequest request,
        io.grpc.stub.StreamObserver<com.codedawn.vital.server.rpc.VitalRPC.VitalRpcResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class VitalRpcServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<VitalRpcServiceBlockingStub> {
    private VitalRpcServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VitalRpcServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VitalRpcServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.codedawn.vital.server.rpc.VitalRPC.VitalRpcResponse send(com.codedawn.vital.server.rpc.VitalRPC.VitalRpcRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class VitalRpcServiceFutureStub extends io.grpc.stub.AbstractFutureStub<VitalRpcServiceFutureStub> {
    private VitalRpcServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected VitalRpcServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new VitalRpcServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.codedawn.vital.server.rpc.VitalRPC.VitalRpcResponse> send(
        com.codedawn.vital.server.rpc.VitalRPC.VitalRpcRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final VitalRpcServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(VitalRpcServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND:
          serviceImpl.send((com.codedawn.vital.server.rpc.VitalRPC.VitalRpcRequest) request,
              (io.grpc.stub.StreamObserver<com.codedawn.vital.server.rpc.VitalRPC.VitalRpcResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class VitalRpcServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    VitalRpcServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.codedawn.vital.server.rpc.VitalRPC.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("VitalRpcService");
    }
  }

  private static final class VitalRpcServiceFileDescriptorSupplier
      extends VitalRpcServiceBaseDescriptorSupplier {
    VitalRpcServiceFileDescriptorSupplier() {}
  }

  private static final class VitalRpcServiceMethodDescriptorSupplier
      extends VitalRpcServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    VitalRpcServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (VitalRpcServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new VitalRpcServiceFileDescriptorSupplier())
              .addMethod(getSendMethod())
              .build();
        }
      }
    }
    return result;
  }
}
