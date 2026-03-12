package com.qchat.tim_chat.io.netty;

import com.qchat.tim_chat.io.netty.handles.ChatHandle;
import com.qchat.tim_chat.io.netty.handles.ExceptionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Netty WebSocket 服务器
 * 负责启动并管理 WebSocket 服务，处理客户端长连接
 * 
 * 主要功能：
 * 1. 绑定端口 8088，监听 WebSocket 连接
 * 2. 配置 Netty ChannelPipeline，添加 HTTP 编解码器、WebSocket 协议处理器
 * 3. 使用主从 Reactor 线程模型（BossGroup + WorkerGroup）
 * 4. 注册自定义消息处理器和异常处理器
 */
@Component
public class WebSocketServer {
    @Autowired
    private ChatHandle chatHandle;
    
    /**
     * 启动 WebSocket 服务器
     * 配置 Netty 引导程序并绑定端口
     */
    public void start() {
        // Boss 线程组：负责接收客户端连接
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        // Worker 线程组：负责处理 I/O 操作
        EventLoopGroup subGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(mainGroup, subGroup)
                    .channel(NioServerSocketChannel.class) // 使用 NIO 模式
                    .option(ChannelOption.SO_BACKLOG, 1024) // 连接队列最大长度
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec()); // HTTP 编解码器
                            pipeline.addLast(new ChunkedWriteHandler()); // 支持大数据流写入
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG)); // 日志处理器
                            pipeline.addLast(new HttpObjectAggregator(65536)); // HTTP 消息聚合器
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws")); // WebSocket 协议处理器，路径/ws
                            pipeline.addLast(chatHandle); // 自定义聊天消息处理器
                            pipeline.addLast(new ExceptionHandler()); // 异常处理器
                        }
                    });

            System.out.println("WebSocket server started...");
            ChannelFuture cf = bootstrap.bind(8088).sync(); // 绑定端口并同步等待
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (cf.isSuccess()) {
                        System.out.println("Listening on port 8088");
                    } else {
                        System.out.println("Failed to listen on port 8088");
                    }
                }
            });
            cf.channel().closeFuture().sync(); // 监听关闭事件
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mainGroup.shutdownGracefully(); // 优雅关闭 Boss 线程组
            subGroup.shutdownGracefully(); // 优雅关闭 Worker 线程组
        }
    }
}