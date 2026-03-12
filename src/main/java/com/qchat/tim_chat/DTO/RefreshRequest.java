package com.qchat.tim_chat.DTO;

import lombok.Data;

/**
 * 刷新 Token 请求数据传输对象
 * 用于接收前端传递的 Refresh Token，进行 Token 刷新操作
 */
@Data
public class RefreshRequest {
    /**
     * 刷新令牌（Refresh Token）
     * 用于生成新的 Access Token
     */
    private String refreshToken;
    // getters/setters
}
