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


    private volatile Channel channel;

    /**
     * 协议类的class
     */
    private Class<? extends Protocol> protocolClass;

    private ProtocolManager protocolManager;

    private ConnectionEventListener connectionEventListener;

    private HeartBeatLauncher heartBeatLauncher;


    private ScheduledExecutorService executorService;


    /**
     * 连接的真正开关，因为连接中断有可能是使用者关闭，或者是意外中断两种原因。后者需要重连，前者需要设置该开关
     */
    private volatile boolean isConnect = true;

    /**
     * 是否认证，true说明已经认证成功，false说明还没有认证
     */
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

    /**
     * 开始连接，isConnect为true才进行连接，可以通过isConnect区分是断线还是主动退出
     */
    public void start() {
        if (!isConnect) {
            return;
        }
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                connectTask();
            }
        }, 0L, VitalGenericOption.RECONNECT_INTERVAL_TIME.value(), TimeUnit.MILLISECONDS);
    }

    private void connectTask() {
        log.info("正在连接服务器。。。");
        init();
        connect();
    }


    /**
     * 关闭资源，多少不代表是用户主动关闭，断线也会触发，isConnect为false才是用户主动关闭
     */
    public void shutdown() {
        this.isAuth = false;
        heartBeatLauncher.shutdown();
        nioEventLoopGroup.shutdownGracefully();
        if (bootstrap != null) {
            bootstrap = null;
        }
        executorService.shutdownNow();
    }


    /**
     * 初始化netty的TCP配置
     */
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

    /**
     * 进行连接
     */
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
                        /**
                         * 发送认证消息
                         */
                        VitalProtobuf.Protocol auth = VitalMessageFactory.createAuth(VitalGenericOption.ID.value(),VitalGenericOption.TOKEN.value());
                        TCPClient.sender.send(auth, new ResponseCallBack<VitalMessageWrapper>() {
                            @Override
                            public void ackArrived(VitalMessageWrapper messageWrapper) {
                                log.info("认证消息送达");
                            }

                            @Override
                            public void exception(VitalMessageWrapper messageWrapper) {
                                log.info("认证消息发生错误：{}",messageWrapper.getMessage().getExceptionMessage().getExtra());
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

    protected ChannelInitializer initializer() {
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

    /**
     *
     * @return 未连接返回null，否则返回channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     *
     * @return 返回和当前channel绑定的connection
     */
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
