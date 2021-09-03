package com.codedawn.vital.client.connector;

import com.codedawn.vital.client.config.ClientVitalGenericOption;
import com.codedawn.vital.client.handler.ClientConnectionEventHandler;
import com.codedawn.vital.client.handler.ClientTCPHandler;
import com.codedawn.vital.client.session.ClientConnectionEventListener;
import com.codedawn.vital.server.callback.ChannelStatusCallBack;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.ProtocolManager;
import com.codedawn.vital.server.session.Connection;
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

    private ClientConnectionEventListener clientConnectionEventListener;

    private ScheduledExecutorService executorService;

    private ChannelStatusCallBack channelStatusCallBack;
    /**
     * 连接的真正开关，因为连接中断有可能是使用者关闭，或者是意外中断两种原因。后者需要重连，前者需要设置该开关
     */
    private volatile boolean isConnect = true;




    public boolean isConnect() {
        return isConnect;
    }

    /**
     * 不继续连接
     * @param isConnect
     * @return
     */
    public TCPConnect setConnect(boolean isConnect) {
        this.isConnect = isConnect;
        return this;
    }
    /**
     * 判断channel是否认证，true说明已经认证成功，false说明还没有认证
     */
    public static boolean isAuth(Channel c) {
        Attribute<Connection> attr = c.attr(Connection.CONNECTION);
        if (attr != null) {
            Connection connection = attr.get();
            if (connection != null) {
                return true;
            }
        }
        return false;
    }

    public TCPConnect() {
    }

    public TCPConnect(Class<? extends Protocol> protocolClass, ProtocolManager protocolManager, ClientConnectionEventListener clientConnectionEventListener) {
        this.protocolClass = protocolClass;
        this.protocolManager = protocolManager;
        this.clientConnectionEventListener = clientConnectionEventListener;
    }

    public TCPConnect(Class<? extends Protocol> protocolClass, ProtocolManager protocolManager, ClientConnectionEventListener clientConnectionEventListener, ChannelStatusCallBack channelStatusCallBack) {
        this.protocolClass = protocolClass;
        this.protocolManager = protocolManager;
        this.clientConnectionEventListener = clientConnectionEventListener;
        this.channelStatusCallBack = channelStatusCallBack;
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
        }, 0L, ClientVitalGenericOption.RECONNECT_INTERVAL_TIME.value(), TimeUnit.MILLISECONDS);
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

        bootstrap.option(ChannelOption.SO_KEEPALIVE, ClientVitalGenericOption.SO_KEEPALIVE.value())
                .option(ChannelOption.TCP_NODELAY, ClientVitalGenericOption.TCP_NODELAY.value())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ClientVitalGenericOption.CONNECT_TIMEOUT_MILLIS.value());
    }

    /**
     * 进行连接
     */
    private void connect() {
        try {
            ChannelFuture future = bootstrap.connect(ClientVitalGenericOption.SERVER_TCP_IP.value(), ClientVitalGenericOption.SERVER_TCP_PORT.value()).sync();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("连接到服务器{}:{}成功", ClientVitalGenericOption.SERVER_TCP_IP.value(), ClientVitalGenericOption.SERVER_TCP_PORT.value());
                        channel = future.channel();
                        //连接成功，不需要连了
                        if (channel != null) {
                            executorService.shutdownNow();
                        }


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
                pipeline.addLast("ConnectionEventHandler",new ClientConnectionEventHandler(clientConnectionEventListener,channelStatusCallBack));
                pipeline.addLast("TCPClientHandler",new ClientTCPHandler(protocolClass,protocolManager));
            }
        };
    }

    /**
     *
     * @return 连接成功返回channel，否则返回null
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


    public TCPConnect setProtocolClass(Class<? extends Protocol> protocolClass) {
        this.protocolClass = protocolClass;
        return this;
    }

    public TCPConnect setProtocolManager(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
        return this;
    }

    public TCPConnect setClientConnectionEventListener(ClientConnectionEventListener clientConnectionEventListener) {
        this.clientConnectionEventListener = clientConnectionEventListener;
        return this;
    }

    public TCPConnect setChannelStatusCallBack(ChannelStatusCallBack channelStatusCallBack) {
        this.channelStatusCallBack = channelStatusCallBack;
        return this;
    }
}
