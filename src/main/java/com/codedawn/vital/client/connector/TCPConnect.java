package com.codedawn.vital.client.connector;

import com.codedawn.vital.client.TCPClient;
import com.codedawn.vital.client.callback.ResponseCallBack;
import com.codedawn.vital.client.handler.TCPClientHandler;
import com.codedawn.vital.factory.VitalMessageFactory;
import com.codedawn.vital.proto.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
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

    private static final int serverPort = 8000;

    private static final String serverIP = "127.0.0.1";

    private NioEventLoopGroup nioEventLoopGroup;


    private Channel channel;

    private Class<? extends Protocol> protocolClass;

    private ProtocolManager protocolManager;

    public TCPConnect(Class<? extends Protocol> protocolClass, ProtocolManager protocolManager) {
        this.protocolClass = protocolClass;
        this.protocolManager = protocolManager;
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
                        VitalProtobuf.Protocol auth = VitalMessageFactory.createAuth();
                        TCPClient.sender.send(auth, new ResponseCallBack<VitalMessageWrapper>() {
                            @Override
                            public void ackArrived(VitalMessageWrapper messageWrapper) {
                                System.out.println("ack");
                                System.out.println(messageWrapper.toString());
                            }
                        });
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
                Protocol protocol = protocolManager.getProtocol(protocolClass.getSimpleName());
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("LoggingHandler",new LoggingHandler());
                pipeline.addLast("ProtobufVarint32FrameDecoder",protocol.getFrameDecode());
                pipeline.addLast("ProtobufVarint32LengthFieldPrepender",protocol.getLengthFieldPrepender());
                pipeline.addLast("ProtobufDecoder",protocol.getDecode());
                pipeline.addLast("ProtobufEncoder",protocol.getEncode());
                pipeline.addLast(new TCPClientHandler(protocol,protocolManager));
            }
        };
    }

    public Channel getChannel() {
        return channel;
    }


}
