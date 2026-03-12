package com.qchat.tim_chat.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 配置类
 * 
 * 提供 API 文档、在线测试功能
 * 访问地址：http://localhost:9090/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tim_Chat 即时通讯系统 API")
                        .version("1.0.0")
                        .description("基于 Spring Boot + Netty 的高性能即时通讯系统，" +
                                "提供实时消息推送、好友关系管理、JWT 安全认证等功能。\n\n" +
                                "**技术栈**:\n" +
                                "- Spring Boot 3.4.4\n" +
                                "- Netty 4.1.115\n" +
                                "- JWT 双 Token 认证\n" +
                                "- Redis 缓存\n" +
                                "- MyBatis-Plus\n\n" +
                                "**主要功能**:\n" +
                                "- 用户登录/注册\n" +
                                "- JWT Token 刷新\n" +
                                "- 好友关系管理\n" +
                                "- 实时消息通信\n" +
                                "- 离线消息同步")
                        .contact(new Contact()
                                .name("Tim_Chat Team")
                                .email("support@timchat.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
