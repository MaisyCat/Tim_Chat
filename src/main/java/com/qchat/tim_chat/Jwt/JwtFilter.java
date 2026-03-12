package com.qchat.tim_chat.Jwt;

import com.qchat.tim_chat.redis.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * 继承 OncePerRequestFilter，确保每个请求只执行一次过滤
 * 
 * Access Token 验证逻辑：
 * 1. 解析请求头中的 Access Token
 * 2. 无 Token → 放行（由 Spring Security 处理未认证情况）
 * 3. Token 在黑名单中 → 返回 401 Unauthorized
 * 4. Token 合法有效 → 设置认证信息，放行
 * 5. Token 已过期 → 返回 401 Unauthorized，前端使用 Refresh Token 刷新
 * 
 * 注意：不在 Token 快过期时主动刷新，而是等过期后由前端调用 refresh 接口
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    
    private final RedisService redisService;
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtFilter(RedisService redisService, JwtUtil jwtUtil) {
        this.redisService = redisService;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * 过滤请求，执行 JWT 验证逻辑
     * 
     * 处理流程：
     * 1. 解析请求头中的 Access Token
     * 2. 无 Token → 放行（由 Spring Security 处理未认证情况）
     * 3. Token 在黑名单中 → 返回 401 Unauthorized
     * 4. Token 合法有效 → 设置认证信息，放行
     * 5. Token 已过期 → 返回 401 Unauthorized，前端使用 Refresh Token 刷新
     * 
     * 注意：
     * - Access Token 包含用户信息，用于业务鉴权
     * - Access Token 过期后，前端调用 /auth/refresh 接口
     * - Refresh Token 不参与业务鉴权，仅用于刷新 Access Token
     * 
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authToken = ResolveToken(request);
        if (authToken == null) {
            // 无 Token，放行请求（Spring Security 会处理未认证情况）
            filterChain.doFilter(request, response);
            return;
        }
        // 检查 Token 是否在黑名单中（已被注销）
        if (redisService.IsInBlacklist(authToken)) {
            sendError(response, "Token Invalid - Token has been revoked");
            return;
        }
        // 验证 Token 有效性（签名、过期时间等）
        if (jwtUtil.validateToken(authToken)) {
            String username = jwtUtil.getUsernameFromToken(authToken);
            
            // Token 有效，设置认证信息
            // 这样后续的 Controller 可以通过 SecurityContextHolder 获取当前用户信息
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            // Token 已过期或无效，返回 401
            // 前端收到 401 后，应使用 Refresh Token 调用 /auth/refresh 接口
            // Refresh Token 不参与业务鉴权，仅用于刷新 Access Token
            sendError(response, "Access Token expired or invalid - Please use refresh token to get new access token");
        }
    }
    
    /**
     * 从请求头中解析 JWT Token
     * @param request HTTP 请求对象
     * @return 解析后的 Token，如果不存在则返回 null
     */
    private String ResolveToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
        return token;
    }
    
    /**
     * 发送错误响应
     * @param resp HTTP 响应对象
     * @param msg 错误消息内容
     */
    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.getWriter().write(msg);
        resp.getWriter().flush();
    }

}
