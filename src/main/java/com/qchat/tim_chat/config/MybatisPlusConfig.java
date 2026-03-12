package com.qchat.tim_chat.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 负责扫描 Mapper 接口，启用 MyBatis-Plus 功能
 */
@Configuration
@MapperScan("com.qchat.tim_chat.mappers")
public class MybatisPlusConfig {
}
