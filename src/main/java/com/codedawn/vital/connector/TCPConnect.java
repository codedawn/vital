package com.codedawn.vital.connector;

import com.codedawn.vital.callback.ResponseCallBack;
import com.codedawn.vital.client.TCPClient;
import com.codedawn.vital.config.VitalGenericOption;
import com.codedawn.vital.factory.VitalMessageFactory;
import com.codedawn.vital.handler.ConnectionEventHandler;
import com.codedawn.vital.handler.TCPClientHandler;
import com.codedawn.vital.proto.Protocol;
import com.codedawn.vital.proto.ProtocolManager;
import com.codedawn.vital.proto.VitalMessageWrapper;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.qos.HeartBeatLauncher;
import com.codedawn.vital.session.Connection;
import com.codedawn.vital.session.ConnectionEventListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author codedawn
 * @date 2021-07-24 10:08
 */
public class TCPConnect {

    private static Logger log = LoggerFactory.getLogger(TCPConnect.class);

    private Bootstrap bootstrap;


    private NioEventLoopGroup nioEventLoopGroup;


    private Channel channel;

    private Class<? extends Protocol> protocolClass;

    private ProtocolManager protocolManager;

    private ConnectionEventListener connectionEventListener;

    private HeartBeatLauncher heartBeatLauncher;


    private ScheduledExecutorService executorService;


    /**
     * 连接的真正开关，因为连接中断有可能是使用者关闭，或者是意外中断两种原因。后者需要重连，前者需要设置该开关
     */
    private boolean isConnect = true;

    private volatile boolean isAuth = false;

    public boolean isConnect() {
        return isConnect;
    }

    /**
     * 不继续连接
     * @param connect
     * @return
     */
    public TCPConnect setConnect(boolean connect) {
        isConnect = connect;
        return this;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public TCPConnect setAuth(boolean auth) {
        isAuth = auth;
        return this;
    }

    public TCPConnect(Class<? extends Protocol> protocolClass, ProtocolManager protocolManager, ConnectionEventListener connectionEventListener) {
        this.protocolClass = protocolClass;
        this.protocolManager = protocolManager;
        this.connectionEventListener = connectionEventListener;
    }

    public TCPConnect setHeartBeatLauncher(HeartBeatLauncher heartBeatLauncher) {
        this.heartBeatLauncher = heartBeatLauncher;
        return this;
    }

    public void start() {
        if (!isConnect) {
            return;
        }
        log.info("正在连接服务器。。。");
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                connectTask();
            }
        }, 0L, VitalGenericOption.RECONNECT_INTERVAL_TIME.value(), TimeUnit.MILLISECONDS);
    }

    private void connectTask() {
        init();
        connect();
    }


    public void shutdown() {
        heartBeatLauncher.shutdown();
        nioEventLoopGroup.shutdownGracefully();
    }


    public void init() {
        nioEventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(initializer());

        bootstrap.option(ChannelOption.SO_KEEPALIVE, VitalGenericOption.SO_KEEPALIVE.value())
                .option(ChannelOption.TCP_NODELAY, VitalGenericOption.TCP_NODELAY.value())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, VitalGenericOption.CONNECT_TIMEOUT_MILLIS.value());
    }

    public void connect() {
        try {
            ChannelFuture future = bootstrap.connect(VitalGenericOption.SERVER_TCP_IP.value(), VitalGenericOption.SERVER_TCP_PORT.value()).sync();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("连接到服务器{}:{}成功",VitalGenericOption.SERVER_TCP_IP.value(),VitalGenericOption.SERVER_TCP_PORT.value());
                        channel = future.channel();
                        //连接成功，不需要连了
                        if (channel != null) {
                            executorService.shutdownNow();
                        }
                        if (heartBeatLauncher != null) {
                            heartBeatLauncher.start();
                        }
                        VitalProtobuf.Protocol auth = VitalMessageFactory.createAuth(VitalGenericOption.ID.value(),VitalGenericOption.TOKEN.value());
                        TCPClient.sender.send(auth, new ResponseCallBack<VitalMessageWrapper>() {
                            @Override
                            public void ackArrived(VitalMessageWrapper messageWrapper) {
                                log.info("认证消息送达");
                            }

                            @Override
                            public void exception(VitalMessageWrapper messageWrapper) {
                                System.out.println(messageWrapper.getMessage().getExceptionMessage().getExtra());
                            }

                        });
                    }else {

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
                pipeline.addLast("ConnectionEventHandler",new ConnectionEventHandler(connectionEventListener));
                pipeline.addLast("TCPClientHandler",new TCPClientHandler(protocol,protocolManager));
            }
        };
    }

    public Channel getChannel() {
        //todo channel不能为空,这时候应该连接？

        return channel;
    }

    public Connection getConnection() {
        Attribute<Connection> attr = channel.attr(Connection.CONNECTION);
        if (attr != null) {
            Connection connection = attr.get();
            if (connection != null) {
                return connection;
            }
        }
        log.info("返回null，说明未认证成功");
        return null;
    }
}
