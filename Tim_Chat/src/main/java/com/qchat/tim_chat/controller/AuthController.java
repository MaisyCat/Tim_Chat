package com.qchat.tim_chat.controller;

// 控制器类
import com.qchat.tim_chat.DTO.LoginRequest;
import com.qchat.tim_chat.DTO.RefreshRequest;
import com.qchat.tim_chat.DTO.TokenResponse;
import com.qchat.tim_chat.Jwt.JwtUtil;
import com.qchat.tim_chat.entities.User;
import com.qchat.tim_chat.redis.RedisService;
import com.qchat.tim_chat.services.UserService;
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
     * @param request 登录请求（包含用户名和密码）
     * @return ResponseEntity 包含 JWT Token 的响应
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        boolean is_pass=userService.check_user_for_login(request.getUsername(), request.getPassword());
        if (!is_pass) {
            return ResponseEntity.status(501).body("用户账号或密码出错");
        }
        String username = request.getUsername();

        String accessToken = jwtUtil.genAccessToken(username);
        String refreshToken = jwtUtil.genRefreshToken(username);

        redisService.saveRefreshToken(refreshToken, username);
        return ResponseEntity.ok(new TokenResponse(true,accessToken, refreshToken));
    }
    /**
     * 用户注册接口
     * @param request 注册请求（包含用户名和密码）
     * @return ResponseEntity 注册结果
     */
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
     * @param request 刷新请求（包含 Refresh Token）
     * @return ResponseEntity 包含新的 JWT Token 对
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!redisService.checkRefreshToken(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String newAccessToken = jwtUtil.genAccessToken(username);
        String newRefreshToken = jwtUtil.genRefreshToken(username);

        // 替换旧refreshToken
        redisService.deleteRefreshToken(refreshToken);
        redisService.saveRefreshToken(newRefreshToken, username);

//        // refreshToken加入黑名单（可选）
//        long expiration = jwtUtil.getExpirationFromToken(refreshToken) - System.currentTimeMillis();
//        redisService.add_to_blacklist(refreshToken, expiration);

        return ResponseEntity.ok(new TokenResponse(true,newAccessToken, newRefreshToken));
    }

}
