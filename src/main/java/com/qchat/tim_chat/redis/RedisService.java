package com.qchat.tim_chat.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    public RedisService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    
    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration; // Refresh Token 过期时间（7 天）

    /**
     * 保存 Refresh Token（仅存储用户名，设备信息在 Token 中）
     * 
     * @param refreshToken 刷新令牌
     * @param username 用户名
     */
    public void saveRefreshToken(String refreshToken, String username) {
        stringRedisTemplate.opsForValue().set(
            "refresh_token:" + refreshToken, 
            username, 
            refreshExpiration, 
            TimeUnit.MILLISECONDS
        );
    }
    /**
     * 检查 Refresh Token 是否有效
     * 
     * @param refreshToken 刷新令牌
     * @return true-有效；false-无效或已过期
     */
    public boolean checkRefreshToken(String refreshToken) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey("refresh_token:" + refreshToken));
    }
    /**
     * 删除 Refresh Token
     * 
     * @param refreshToken 刷新令牌
     */
    public void deleteRefreshToken(String refreshToken) {
        stringRedisTemplate.delete("refresh_token:" + refreshToken);
    }
    public void add_to_blacklist(String token,long expirationMs) {
        stringRedisTemplate.opsForValue().set("blacklist:"+token,"valid:"+expirationMs);
    }
    public boolean IsInBlacklist(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey("blacklist:"+token));
    }
}
