package com.codedawn.vital.connector;

import com.codedawn.vital.config.VitalGenericOption;
import com.codedawn.vital.handler.AuthHandler;
import com.codedawn.vital.handler.ConnectionEventHandler;
import com.codedawn.vital.handler.TCPBusHandler;
import com.codedawn.vital.proto.Protocol;
import com.codedawn.vital.proto.ProtocolManager;
import com.codedawn.vital.session.ConnectionEventListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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


    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup();


    private Channel serverChannel;

    private ServerBootstrap serverBootstrap;

    private Class<? extends Protocol> protocolClass;

    private ProtocolManager protocolManager;

    private ConnectionEventListener connectionEventListener;

    public TCPConnector(Class<? extends Protocol> protocolClass, ProtocolManager protocolManager,ConnectionEventListener connectionEventListener) {
        this.protocolClass = protocolClass;
        this.protocolManager = protocolManager;
        this.connectionEventListener = connectionEventListener;
    }


    /**
     * 启动
     */
    public void start() {
        init();
        bind();
    }

    /**
     * 初始化netty的TCP配置
     */
    private void init() {
        serverBootstrap = new ServerBootstrap()
                .group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(serverInitializer());

        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, VitalGenericOption.SO_KEEPALIVE.value());
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, VitalGenericOption.TCP_NODELAY.value());
    }


    /**
     * 绑定服务器端口
     */
    private void bind() {
        try {
            ChannelFuture future = serverBootstrap.bind(VitalGenericOption.SERVER_TCP_PORT.value()).sync();
            if (future.isSuccess()) {
                serverChannel = future.channel();
                log.info("TcpConnector绑定端口{}成功",VitalGenericOption.SERVER_TCP_PORT.value());
                ChannelFuture closeFuture = serverChannel.closeFuture();
                closeFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        bossGroup.shutdownGracefully();
                        workerGroup.shutdownGracefully();
                    }
                });

                log.info("tcp服务正在{}端口进行监听",VitalGenericOption.SERVER_TCP_PORT.value());
            }
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    protected ChannelHandler serverInitializer() {
        ChannelInitializer<SocketChannel> channelInitializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                Protocol vitalProtocol = protocolManager.getProtocol(protocolClass.getSimpleName());
                pipeline.addLast("LoggingHandler",new LoggingHandler())
                        .addLast("ProtobufVarint32FrameDecoder",vitalProtocol.getFrameDecode())
                        .addLast("ProtobufVarint32LengthFieldPrepender",vitalProtocol.getLengthFieldPrepender())
                        .addLast("ProtobufDecoder",vitalProtocol.getDecode())
                        .addLast("ProtobufEncoder",vitalProtocol.getEncode())
                        .addLast("ReadTimeoutHandler",new ReadTimeoutHandler(VitalGenericOption.SERVER_READ_TIMEOUT.value()))
                        .addLast("AuthHandler",new AuthHandler(protocolClass,protocolManager))
                        .addLast("ConnectionEventHandler",new ConnectionEventHandler(connectionEventListener))
                        .addLast("TCPBusHandler",new TCPBusHandler(protocolClass,protocolManager));

            }
        };

        return channelInitializer;
    }


    /**
     * 关闭
     */
    public void shutdown() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (serverBootstrap != null) {
            serverBootstrap = null;
        }
    }

}
