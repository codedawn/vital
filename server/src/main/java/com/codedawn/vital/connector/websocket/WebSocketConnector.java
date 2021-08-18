package com.codedawn.vital.connector.websocket;

import com.codedawn.vital.config.VitalGenericOption;
import com.codedawn.vital.handler.AuthHandler;
import com.codedawn.vital.handler.ConnectionEventHandler;
import com.codedawn.vital.handler.TCPBusHandler;
import com.codedawn.vital.proto.Protocol;
import com.codedawn.vital.proto.ProtocolManager;
import com.codedawn.vital.proto.VitalProtobuf;
import com.codedawn.vital.session.ConnectionEventListener;
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

    }


    /**
     * 绑定服务器端口
     */
    private void bind() {
        try {
            ChannelFuture future = serverBootstrap.bind(VitalGenericOption.SERVER_WEBSOCKET_PORT.value()).sync();
            if (future.isSuccess()) {
                serverChannel = future.channel();
                log.info("WebSocketConnector绑定端口{}成功",VitalGenericOption.SERVER_WEBSOCKET_PORT.value());
                ChannelFuture closeFuture = serverChannel.closeFuture();
                closeFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        bossGroup.shutdownGracefully();
                        workerGroup.shutdownGracefully();
                    }
                });

                log.info("WebSocket服务正在{}端口进行监听",VitalGenericOption.SERVER_WEBSOCKET_PORT.value());
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

                // HTTP请求的解码和编码
                pipeline.addLast(new HttpServerCodec());
                // 把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse，
                // 原因是HTTP解码器会在每个HTTP消息中生成多个消息对象HttpRequest/HttpResponse,HttpContent,LastHttpContent
                pipeline.addLast(new HttpObjectAggregator(65536));
                // 主要用于处理大数据流，比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的; 增加之后就不用考虑这个问题了
                pipeline.addLast(new ChunkedWriteHandler());
                // WebSocket数据压缩
                pipeline.addLast(new WebSocketServerCompressionHandler());
                // 协议包长度限制
                pipeline.addLast(new WebSocketServerProtocolHandler("/websocket", null, true));
                // 协议包解码
                pipeline.addLast(new MessageToMessageDecoder<WebSocketFrame>() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> objs) throws Exception {
                        ByteBuf buf = ((BinaryWebSocketFrame) frame).content();
                        objs.add(buf);
                        buf.retain();
                    }
                });
                // 协议包编码
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

                        // ==== 上面代码片段是拷贝自TCP ProtobufEncoder 源码 ====
                        // 然后下面再转成websocket二进制流，因为客户端不能直接解析protobuf编码生成的

                        WebSocketFrame frame = new BinaryWebSocketFrame(result);
                        out.add(frame);
                    }
                });

                // 协议包解码时指定Protobuf字节数实例化为CommonProtocol类型
                pipeline.addLast(new ProtobufDecoder(VitalProtobuf.Protocol.getDefaultInstance()));

                // websocket定义了传递数据的6中frame类型

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
