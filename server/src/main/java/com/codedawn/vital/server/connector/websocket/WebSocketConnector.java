package com.codedawn.vital.server.connector.websocket;

import com.codedawn.vital.server.config.VitalGenericOption;
import com.codedawn.vital.server.handler.AuthHandler;
import com.codedawn.vital.server.handler.ConnectionEventHandler;
import com.codedawn.vital.server.handler.TCPBusHandler;
import com.codedawn.vital.server.proto.Protocol;
import com.codedawn.vital.server.proto.ProtocolManager;
import com.codedawn.vital.server.proto.VitalPB;
import com.codedawn.vital.server.session.ConnectionEventListener;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * @author codedawn
 * @date 2021-08-08 19:10
 */
public class WebSocketConnector {
    private static Logger log = LoggerFactory.getLogger(WebSocketConnector.class);


    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);

    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup();


    private Channel serverChannel;

    private ServerBootstrap serverBootstrap;

    private Class<? extends Protocol> protocolClass;

    private ProtocolManager protocolManager;

    private ConnectionEventListener connectionEventListener;

    public WebSocketConnector(Class<? extends Protocol> protocolClass, ProtocolManager protocolManager,ConnectionEventListener connectionEventListener) {
        this.protocolClass = protocolClass;
        this.protocolManager = protocolManager;
        this.connectionEventListener = connectionEventListener;
    }


    /**
     * ??????
     */
    public void start() {
        init();
        bind();
    }

    /**
     * ?????????netty???TCP??????
     */
    private void init() {
        serverBootstrap = new ServerBootstrap()
                .group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(serverInitializer());

    }


    /**
     * ?????????????????????
     */
    private void bind() {
        try {
            ChannelFuture future = serverBootstrap.bind(VitalGenericOption.SERVER_WEBSOCKET_PORT.value()).sync();
            if (future.isSuccess()) {
                serverChannel = future.channel();
                log.info("WebSocketConnector????????????{}??????",VitalGenericOption.SERVER_WEBSOCKET_PORT.value());
                ChannelFuture closeFuture = serverChannel.closeFuture();
                closeFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        bossGroup.shutdownGracefully();
                        workerGroup.shutdownGracefully();
                    }
                });

                log.info("WebSocket????????????{}??????????????????",VitalGenericOption.SERVER_WEBSOCKET_PORT.value());
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

                // HTTP????????????????????????
                pipeline.addLast(new HttpServerCodec());
                // ???????????????????????????????????????FullHttpRequest??????FullHttpResponse???
                // ?????????HTTP?????????????????????HTTP?????????????????????????????????HttpRequest/HttpResponse,HttpContent,LastHttpContent
                pipeline.addLast(new HttpObjectAggregator(65536));
                // ?????????????????????????????????????????????1G???????????????????????????????????????????????????jvm?????????; ??????????????????????????????????????????
                pipeline.addLast(new ChunkedWriteHandler());
                // WebSocket????????????
                pipeline.addLast(new WebSocketServerCompressionHandler());
                // ?????????????????????
                pipeline.addLast(new WebSocketServerProtocolHandler("/websocket", null, true));
                // ???????????????
                pipeline.addLast(new MessageToMessageDecoder<WebSocketFrame>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> objs) throws Exception {
                        ByteBuf buf = ((BinaryWebSocketFrame) frame).content();
                        objs.add(buf);
                        buf.retain();
                    }
                });
                // ???????????????
                pipeline.addLast(new MessageToMessageEncoder<MessageLiteOrBuilder>() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
                        ByteBuf result = null;
                        if (msg instanceof MessageLite) {
                            result = wrappedBuffer(((MessageLite) msg).toByteArray());
                        }
                        if (msg instanceof MessageLite.Builder) {
                            result = wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
                        }

                        // ==== ??????????????????????????????TCP ProtobufEncoder ?????? ====
                        // ?????????????????????websocket????????????????????????????????????????????????protobuf???????????????

                        WebSocketFrame frame = new BinaryWebSocketFrame(result);
                        out.add(frame);
                    }
                });

                // ????????????????????????Protobuf?????????????????????CommonProtocol??????
                pipeline.addLast(new ProtobufDecoder(VitalPB.Frame.getDefaultInstance()));

                // websocket????????????????????????6???frame??????

                pipeline
                        .addLast("ReadTimeoutHandler",new ReadTimeoutHandler(VitalGenericOption.SERVER_READ_TIMEOUT.value()))
                        .addLast("AuthHandler",new AuthHandler(protocolClass,protocolManager))
                        .addLast("ConnectionEventHandler",new ConnectionEventHandler(connectionEventListener))
                        .addLast("TCPBusHandler",new TCPBusHandler(protocolClass,protocolManager));
            }
        };

        return channelInitializer;
    }


    /**
     * ??????
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
