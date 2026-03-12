package com.qchat.tim_chat.DTO;

/**
 * Token 响应数据传输对象
 * 用于向客户端返回 JWT Token 信息
 * 
 * @param success      是否成功生成 Token
 * @param accessToken  访问令牌，用于 API 请求认证（有效期 1 小时）
 * @param refreshToken 刷新令牌，用于获取新的 Access Token（有效期 24 小时）
 */
public record TokenResponse(boolean success, String accessToken, String refreshToken) {

}