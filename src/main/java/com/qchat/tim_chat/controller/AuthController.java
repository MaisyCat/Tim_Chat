package com.qchat.tim_chat.controller;

// 控制器类
import com.qchat.tim_chat.DTO.LoginRequest;
import com.qchat.tim_chat.DTO.RefreshRequest;
import com.qchat.tim_chat.DTO.TokenResponse;
import com.qchat.tim_chat.Jwt.JwtUtil;
import com.qchat.tim_chat.entities.User;
import com.qchat.tim_chat.redis.RedisService;
import com.qchat.tim_chat.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

/**
 * 认证控制器
 * 处理用户认证相关的 HTTP 请求
 * 
 * 主要功能：
 * 1. 用户登录 - 验证账号密码，生成 JWT Token
 * 2. 用户注册 - 创建新用户账号
 * 3. Token 刷新 - 使用 Refresh Token 获取新的 Access Token
 */
@Tag(name = "认证管理", description = "用户登录、注册、Token 刷新等认证相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtUtil;

    private final RedisService redisService;

    private final UserService userService;
    @Autowired
    public AuthController(JwtUtil jwtUtil, RedisService redisService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.redisService = redisService;
        this.userService = userService;
    }

    /**
     * 用户登录接口
     * 
     * 处理流程：
     * 1. 验证用户名密码是否正确
     * 2. 生成 Access Token（有效期 2 小时）和 Refresh Token（有效期 7 天）
     * 3. 将 Refresh Token 与设备指纹绑定后存入 Redis
     * 4. 返回双 Token 给前端
     * 
     * Token 说明：
     * - Access Token：包含丰富用户信息，用于业务鉴权，有效期短（30 分钟 -2 小时）
     * - Refresh Token：仅用于刷新 Access Token，不参与业务鉴权，有效期长（7-10 天）
     * 
     * @param request 登录请求（包含用户名和密码）
     * @param deviceCode 设备编码（DeviceID + OAID 的混合编码），从请求头获取
     * @return ResponseEntity 包含 JWT Token 的响应
     */
    @Operation(summary = "用户登录", description = "验证用户名密码，成功则返回 Access Token 和 Refresh Token")
    @PostMapping("/login")
    public ResponseEntity<?> login(
        @RequestBody LoginRequest request,
        @Parameter(description = "设备编码（DeviceID + OAID 的混合编码）", example = "MI6-ABC123XYZ-OAID987654", required = true)
        @RequestHeader(value = "X-Device-Code", required = true) String deviceCode
    ) {

        boolean is_pass=userService.check_user_for_login(request.getUsername(), request.getPassword());
        if (!is_pass) {
            return ResponseEntity.status(501).body("用户账号或密码出错");
        }
        String username = request.getUsername();

        // 生成双 Token：Access Token + Refresh Token（包含设备编码）
        String accessToken = jwtUtil.genAccessToken(username);
        String refreshToken = jwtUtil.genRefreshToken(username, deviceCode);

        // 将 Refresh Token 存入 Redis（仅存储用户名，设备信息在 Token 中）
        redisService.saveRefreshToken(refreshToken, username);

        return ResponseEntity.ok(new TokenResponse(true,accessToken, refreshToken));
    }
    /**
     * 用户注册接口
     * @param request 注册请求（包含用户名和密码）
     * @return ResponseEntity 注册结果
     */
    @Operation(summary = "用户注册", description = "创建新用户账号")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        // 这里应添加用户认证逻辑（如数据库验证）
        boolean is_pass=userService.check_user_for_signup(request.getUsername());
        if (is_pass) {
            return ResponseEntity.status(501).body("此用户已注册");
        }
        String username = request.getUsername();
        String password = request.getPassword();
        Timestamp create_time = new Timestamp(System.currentTimeMillis());
        User user=User.builder()
                .username(username)
                .password(password)
                .create_time(create_time)
                .build();
        if(!userService.save_user(user)){
            return ResponseEntity.ok("注册失败");
        }
        return ResponseEntity.ok("注册成功");

    }


    /**
     * 刷新 Token 接口
     * 
     * Access Token 过期后的刷新流程：
     * 1. 前端检测到 Access Token 过期（收到 401 响应）
     * 2. 前端使用本地存储的 Refresh Token 调用此接口
     * 3. 后端验证 Refresh Token 有效性（是否在 Redis 中、是否过期）
     * 4. 从 Refresh Token 中解析设备编码
     * 5. 验证设备编码是否与请求头中的设备编码一致
     * 6. 验证通过则生成新的 Access Token + Refresh Token 对
     * 7. 旧 Refresh Token 失效，防止重用攻击
     * 8. 前端保存新 Token，自动重试失败的业务请求
     * 
     * 重要特性：
     * - Refresh Token 包含设备编码（DeviceID + OAID），直接嵌入在 Token 中
     * - Refresh Token 不参与业务鉴权，仅用于刷新 Access Token
     * - Refresh Token 有效期 7-10 天，减少用户登录频率
     * - Access Token 包含丰富用户信息，有效期短（30 分钟 -2 小时）
     * 
     * @param request 刷新请求（包含 Refresh Token）
     * @param deviceCode 设备编码（DeviceID + OAID 的混合编码），从请求头获取
     * @return ResponseEntity 包含新的 JWT Token 对
     */
    @Operation(summary = "刷新 Token", description = "Access Token 过期后，使用 Refresh Token 获取新的 Token 对")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
        @RequestBody RefreshRequest request,
        @Parameter(description = "设备编码（DeviceID + OAID 的混合编码）", example = "MI6-ABC123XYZ-OAID987654")
        @RequestHeader(value = "X-Device-Code", required = false) String deviceCode
    ) {
        String refreshToken = request.getRefreshToken();
        
        // 1. 验证 Refresh Token 是否存在于 Redis 中（有效且未过期）
        if (!redisService.checkRefreshToken(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid refresh token - Please login again");
        }
        
        // 2. 验证 Token 签名和过期时间
        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("Refresh token expired or invalid");
        }

        // 3. 从 Refresh Token 中解析用户名和设备编码
        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String tokenDeviceCode = jwtUtil.getDeviceCodeFromToken(refreshToken);
        
        // 4. 验证设备编码是否匹配（防止跨设备盗用）
        if (deviceCode == null || deviceCode.isEmpty()) {
            return ResponseEntity.status(401).body("Device code is required");
        }
        
        if (!deviceCode.equals(tokenDeviceCode)) {
            // 设备编码不匹配，可能是 Refresh Token 被盗用
            return ResponseEntity.status(401)
                .body("Device code mismatch - Possible token theft detected. " +
                      "Expected: " + tokenDeviceCode + ", Got: " + deviceCode);
        }
        
        // 5. 生成新的 Token 对（同时刷新 Access Token 和 Refresh Token）
        String newAccessToken = jwtUtil.genAccessToken(username);
        String newRefreshToken = jwtUtil.genRefreshToken(username, deviceCode);

        // 6. 删除旧 Refresh Token，防止重用攻击
        redisService.deleteRefreshToken(refreshToken);
        
        // 7. 存储新 Refresh Token
        redisService.saveRefreshToken(newRefreshToken, username);

//        // refreshToken 加入黑名单（可选增强安全）
//        long expiration = jwtUtil.getExpirationFromToken(refreshToken) - System.currentTimeMillis();
//        redisService.add_to_blacklist(refreshToken, expiration);

        // 返回新的 Token 对给前端
        return ResponseEntity.ok(new TokenResponse(true, newAccessToken, newRefreshToken));
    }

}
