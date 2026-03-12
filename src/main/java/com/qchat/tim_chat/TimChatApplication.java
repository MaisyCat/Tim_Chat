package com.qchat.tim_chat;

import com.qchat.tim_chat.io.netty.WebSocketServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Tim_Chat 应用程序主启动类
 * 
 * @SpringBootApplication 标记为 Spring Boot 应用，启用自动配置和组件扫描
 * @EnableAsync 启用异步方法支持，用于处理并发任务
 * 
 * 主要职责：
 * 1. 启动 Spring Boot 应用
 * 2. 注册 WebSocketServer Bean
 * 3. 应用启动后自动启动 WebSocket 服务器
 */
@SpringBootApplication
@EnableAsync
public class TimChatApplication {

    /**
     * 程序主入口
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(TimChatApplication.class, args);
    }
    
    /**
     * 注册 WebSocketServer Bean
     * @return WebSocketServer 实例
     */
    @Bean
    public WebSocketServer webSocketServer() {
        return new WebSocketServer();
    }


    /**
     * 应用启动后自动执行 WebSocket 服务器
     * @param webSocketServer WebSocket 服务器实例
     * @return CommandLineRunner 执行器
     */
    @Bean
    public CommandLineRunner startServers(WebSocketServer webSocketServer) {
        return args -> {
            webSocketServer.start();
        };
    }
}
