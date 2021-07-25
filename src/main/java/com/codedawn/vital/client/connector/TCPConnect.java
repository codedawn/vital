package com.codedawn.vital.client.connector;

import com.codedawn.vital.client.factory.Send;
import com.codedawn.vital.client.factory.SendFactory;
import com.codedawn.vital.client.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codedawn
 * @date 2021-07-24 10:08
 */
public class TCPConnect {

    private static Logger log = LoggerFactory.getLogger(TCPConnect.class);

    private Bootstrap bootstrap;

    private static final int serverPort = 7091;

    private static final String serverIP = "127.0.0.1";

    private NioEventLoopGroup nioEventLoopGroup;


    private Channel channel;

    private TCPConnect() {

    }

    private static TCPConnect instance;

    public static TCPConnect getInstance() {
        if (instance == null) {
            instance = new TCPConnect();
        }
        return instance;
    }

    public void start() {
        init();
        connect();
    }
    public void init() {
        nioEventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(initializer());

        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5 * 1000);
    }

    public void connect() {
        try {
            ChannelFuture future = bootstrap.connect(serverIP, serverPort);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("连接到服务器{}:{}成功",serverIP,serverPort);
                        channel = future.channel();
                        VitalProtocol.Protocol auth = SendFactory.createAuth();
                        Send.getInstance().send(auth);
                    }
                }
            });
            ChannelFuture closeFuture = future.channel().closeFuture();
            closeFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("断开与服务器连接");
                    }
                    nioEventLoopGroup.shutdownGracefully();
                    channel = null;
                }
            });
        } catch (Exception e) {
            nioEventLoopGroup.shutdownGracefully();
            this.channel = null;
        }


    }

    private ChannelInitializer initializer() {
        return new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("LoggingHandler",new LoggingHandler());
                pipeline.addLast("ProtobufVarint32FrameDecoder",new ProtobufVarint32FrameDecoder());
                pipeline.addLast("ProtobufVarint32LengthFieldPrepender",new ProtobufVarint32LengthFieldPrepender());
                pipeline.addLast("ProtobufDecoder",new ProtobufDecoder(VitalProtocol.Protocol.getDefaultInstance()));
                pipeline.addLast("ProtobufEncoder",new ProtobufEncoder());
                pipeline.addLast(new ClientHandler());
            }
        };
    }

    public Channel getChannel() {
        return channel;
    }


}
