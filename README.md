# Tim_Chat 即时通讯系统

<div align="center">

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Netty](https://img.shields.io/badge/Netty-4.1.115-red.svg)](https://netty.io/)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.11-orange.svg)](https://baomidou.com/)
[![Redis](https://img.shields.io/badge/Redis-6.0+-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

</div>

---

## 📖 项目简介

Tim_Chat 是一款基于 **Spring Boot 3** + **Netty** 的高性能即时通讯系统，采用前后端分离架构，提供实时消息推送、好友关系管理、JWT 安全认证等核心功能。
目前仅支持文字聊天

### ✨ 主要特性

- 🔐 **JWT 双 Token 认证** - Access Token + Refresh Token，安全无感知刷新
- 💬 **实时消息推送** - 基于 Netty WebSocket，支持并发连接
- 👥 **好友关系管理** - 完整的好友申请、确认、拒绝流程
- 📱 **离线消息同步** - 基于时间戳的增量消息拉取机制
- 🚀 **高性能架构** - NIO 异步 IO，主从 Reactor 线程模型
- 💾 **Redis 缓存** - 集中式 Token 管理，支持分布式部署

---

## 🛠️ 技术栈

### 后端核心技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 开发语言 |
| Spring Boot | 3.4.4 | 基础框架 |
| Spring Security | 6.4.5 | 安全认证 |
| JWT | 0.12.6 | Token 生成与验证 |
| Netty | 4.1.115 | WebSocket 通信 |
| MyBatis-Plus | 3.5.11 | 数据持久层 |
| Redis | 6.0+ | 缓存服务 |
| Druid | 1.2.8 | 数据库连接池 |
| MySQL | 8.0+ | 关系型数据库 |

### 开发工具
- **构建工具**: Maven 3.6+
- **IDE 推荐**: IntelliJ IDEA / Eclipse
- **API 测试**: Postman / ApiFox

---

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────┐
│                    Client Layer                     │
│                  (Android)                          │
└───────────────────┬─────────────────────────────────┘
                    │ HTTP/WebSocket
┌───────────────────▼─────────────────────────────────┐
│                   Gateway Layer                     │
│              (Spring Security + JWT)                │
└───────────────────┬─────────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────────┐
│                 Application Layer                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐           │
│  │   Auth   │  │   Chat   │  │ Relation │           │
│  │ Controller│  │Controller│  │Controller│          │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘           │
│       │             │             │                 │
│  ┌────▼─────┐  ┌────▼─────┐  ┌────▼─────┐           │
│  │   Auth   │  │  Chat    │  │Relation  │           │
│  │ Service  │  │ Service  │  │ Service  │           │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘           │
└───────┼─────────────┼─────────────┼─────────────────┘
        │             │             │
┌───────▼─────────────▼─────────────▼─────────────────┐
│                  Data Access Layer                  │
│            (MyBatis-Plus + Mapper)                  │
└───────┬─────────────┬─────────────┬─────────────────┘
        │             │             │
┌───────▼──────┐ ┌───▼──────────┐ ┌▼────────────────┐
│    MySQL     │ │    Redis     │ │   Netty WS      │
│  (Database)  │ │   (Cache)    │ │  (Port 8088)    │
└──────────────┘ └──────────────┘ └─────────────────┘
```


## 🚀 快速开始

### 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 安装步骤

#### 1. 克隆项目
```bash
git clone https://github.com/MaisyCat/Tim_Chat.git
cd Tim_Chat
```

#### 2. 创建数据库
```sql
CREATE DATABASE Qchat CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 修改配置文件
编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/Qchat?useUnicode=true&characterEncoding=UTF-8
    username: root
    password: your_password
  data:
    redis:
      host: 127.0.0.1
      port: 6379
```

#### 4. 初始化数据库表
```
执行sql
```

#### 5. 编译打包
```bash
mvn clean package -DskipTests
```

#### 6. 启动应用
```bash
java -jar target/Tim_Chat-0.0.1-SNAPSHOT.war
```

启动成功后会看到：
```
WebSocket server started...
Listening on port 8088
Tomcat started on port(s): 9090 (http)
```

---

## 📡 API 接口文档
见swagger文档
http://localhost:9090/swagger-ui.html

## 🔑 核心功能详解

### 1. JWT 双 Token 认证机制

```
┌─────────────────────────────────────────────────────┐
│                  登录流程                           │
├─────────────────────────────────────────────────────┤
│ 1. 用户输入账号密码                                 │
│ 2. 后端验证 credentials                             │
│ 3. 生成 Access Token (2 小时) + Refresh Token (7天) │
│ 4. Refresh Token 存入 Redis                         │
│ 5. 返回 Tokens 给前端                               │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│                Token 刷新流程                       │
├─────────────────────────────────────────────────────┤
│ 1. Access Token 过期 → 返回 401                     │
│ 2. 前端自动调用 /auth/refresh                       │
│ 3. 后端验证 Redis 中的 Refresh Token                │
│ 4. 生成新的 Access + Refresh Token                  │
│ 5. 旧 Refresh Token 失效                            │
└─────────────────────────────────────────────────────┘
```

### 2. WebSocket 消息推送

**连接建立**:
```
Client                          Server
  │                              │
  ├────── HTTP Upgrade ─────────>│
  │   (WebSocket Handshake)      │
  │                              │
  │<───── 101 Switching ─────────┤
  │      Protocols               │
  │                              │
  │═══════ WebSocket Connected ══│
```


## 📄 开源协议

本项目采用 MIT 协议开源 - 查看 [LICENSE](LICENSE) 文件了解详情。

---



<div align="center">


</div>
