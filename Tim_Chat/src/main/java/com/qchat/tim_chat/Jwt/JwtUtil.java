package com.qchat.tim_chat.Jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具类
 * 负责生成、验证和解析 JWT Token
 * 支持 Access Token 和 Refresh Token 两种类型
 */
@Component
public class JwtUtil {
    
    /**
     * JWT 签名算法：HS256
     */
    private final SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;

    /**
     * JWT 密钥（从配置文件读取）
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * HMAC-SHA 密钥对象
     */
    private SecretKey KEY;

    /**
     * JWT 主题标识
     */
    private final String SUBJECT = "Peripherals";

    /**
     * Access Token 过期时间（毫秒），默认 1 小时
     */
    @Value("${jwt.access.expiration}")
    private long accessExpiration;

    /**
     * Refresh Token 过期时间（毫秒），默认 24 小时
     */
    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    /**
     * 初始化方法，在 Bean 创建后自动执行
     * 根据配置的密钥字符串生成 HMAC-SHA 密钥对象
     */
    @PostConstruct
    public void init() {
        this.KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成 Access Token
     * @param username 用户名（作为 JWT Claim）
     * @return JWT Access Token 字符串
     */
    public String genAccessToken(String username) {
//        String uuid = UUID.randomUUID().toString();
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "Hs256")
                .and()
                .claim("username", username)
                // 令牌 ID
//                .id(uuid)
                .subject(SUBJECT)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(KEY, ALGORITHM)
                .compact();
    }

    /**
     * 生成 Refresh Token
     * @param username 用户名（作为 JWT Claim）
     * @return JWT Refresh Token 字符串
     */
    public String genRefreshToken(String username) {
//        String uuid = UUID.randomUUID().toString();
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "Hs256")
                .and()
                .claim("username", username)
                // 令牌 ID
//                .id(uuid)
                .subject(SUBJECT)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(KEY, ALGORITHM)
                .compact();
    }

    /**
     * 验证 JWT Token 是否有效
     * @param token JWT Token 字符串
     * @return true-有效；false-无效（过期、格式错误等）
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parse(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从 JWT Token 中获取用户名
     * @param token JWT Token 字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }
    
    /**
     * 检查 Token 是否即将过期
     * @param token JWT Token 字符串
     * @param remainingMillis 剩余时间阈值（毫秒）
     * @return true-即将过期；false-未到期
     */
    public boolean isTokenExpiringSoon(String token, long remainingMillis) {
        long expiration = getExpirationFromToken(token);
        long remainingTime = expiration - System.currentTimeMillis();
        return remainingTime <= remainingMillis;
    }


    /**
     * 获取 JWT Token 的过期时间戳
     * @param token JWT Token 字符串
     * @return 过期时间戳（毫秒）
     */
    public long getExpirationFromToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("exp", Long.class);
    }
}
