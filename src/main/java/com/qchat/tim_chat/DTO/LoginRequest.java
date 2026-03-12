package com.qchat.tim_chat.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录请求数据传输对象
 * 用于接收前端传递的用户登录凭证信息
 */
@Data
@Schema(description = "登录请求")
public class LoginRequest {
    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "testuser", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    
    /**
     * 密码
     */
    @Schema(description = "密码", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;



    // getters/setters


}