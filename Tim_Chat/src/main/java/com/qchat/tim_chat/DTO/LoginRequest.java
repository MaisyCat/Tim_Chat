package com.qchat.tim_chat.DTO;

import lombok.Data;

/**
 * 登录请求数据传输对象
 * 用于接收前端传递的用户登录凭证信息
 */
@Data
public class LoginRequest {
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;

    // getters/setters


}