package com.codedawn.vital.server.connector;

import com.codedawn.vital.server.handler.AuthHandler;
import com.codedawn.vital.server.handler.TCPBusHandler;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.ProtocolManager;
import com.codedawn.vital.server.proto.VitalProtocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-21 10:24
 */
public class TCPConnector {

    private static Logger log = LoggerFactory.getLogger(TCPConnector.class);


    private static final int READ_TIMEOUT=10;

    private static int port=7091;


    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup();


    private Channel serverChannel;

    private ServerBootstrap serverBootstrap;


    public void start() {
        init();
        bind();
    }

    private void init() {
        serverBootstrap = new ServerBootstrap()
                .group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(serverInitializer());

        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
    }


    private void bind() {
        try {
            ChannelFuture future = serverBootstrap.bind(port).sync();
            if (future.isSuccess()) {
                serverChannel = future.channel();
                log.info("TcpConnector绑定端口{}成功",port);
                ChannelFuture closeFuture = serverChannel.closeFuture();
                closeFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        bossGroup.shutdownGracefully();
                        workerGroup.shutdownGracefully();
                    }
                });

                log.info("tcp服务正在{}端口进行监听",port);
            }
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    private ChannelHandler serverInitializer() {
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                Protocol vitalProtocol = ProtocolManager.getProtocol(VitalProtocol.class.getSimpleName());
                pipeline.addLast("LoggingHandler",new LoggingHandler())
                        .addLast("ProtobufVarint32FrameDecoder",new ProtobufVarint32FrameDecoder())
                        .addLast("ProtobufVarint32LengthFieldPrepender",new ProtobufVarint32LengthFieldPrepender())
                        .addLast("ProtobufDecoder",vitalProtocol.getDecode())
                        .addLast("ProtobufEncoder",vitalProtocol.getEncode())
                        .addLast("ReadTimeoutHandler",new ReadTimeoutHandler(READ_TIMEOUT))
                        .addLast("AuthHandler",new AuthHandler())
                        .addLast("TCPBusHandler",new TCPBusHandler());

            }
        };

        return channelInitializer;
    }


    private void shutdown() {
        if (serverChannel != null) {
            serverChannel.close();
        }
    }

}
