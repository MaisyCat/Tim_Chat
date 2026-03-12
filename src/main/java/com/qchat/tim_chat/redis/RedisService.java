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
    private long refreshExpiration;

    public void saveRefreshToken(String refreshToken,String username) {
        stringRedisTemplate.opsForValue().set("refresh_token:"+refreshToken,username,refreshExpiration, TimeUnit.SECONDS);
    }
    public boolean checkRefreshToken(String refreshToken) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey("refresh_token:" + refreshToken));
    }
    public void deleteRefreshToken(String refreshToken) {
        stringRedisTemplate.delete("refresh_token:"+refreshToken);
    }
    public void add_to_blacklist(String token,long expirationMs) {
        stringRedisTemplate.opsForValue().set("blacklist:"+token,"valid:"+expirationMs);
    }
    public boolean IsInBlacklist(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey("blacklist:"+token));
    }
}
