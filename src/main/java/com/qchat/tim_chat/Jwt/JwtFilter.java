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
 * 主要职责：
 * 1. 解析请求头中的 JWT Token
 * 2. 验证 Token 有效性（是否在黑名单、是否过期）
 * 3. 自动刷新即将过期的 Token（剩余时间少于 30 分钟）
 * 4. 将认证信息注入 Spring Security 上下文
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
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authToken = ResolveToken(request);
        if (authToken == null) {
            // 无 Token，放行请求
            filterChain.doFilter(request, response);
            return;
        }
        // 检查 Token 是否在黑名单中
        if (redisService.IsInBlacklist(authToken)) {
            sendError(response, "Token Invalid");
            return;
        }
        // 验证 Token 有效性
        if (jwtUtil.validateToken(authToken)) {
            String username = jwtUtil.getUsernameFromToken(authToken);
            // 检查 Token 是否即将过期（30 分钟内），如需要自动刷新
            if (jwtUtil.isTokenExpiringSoon(authToken, 30 * 60 * 1000)) { // 30 分钟
                // 生成新 Token
                String newToken = jwtUtil.genAccessToken(username);
//                String newRefreshToken = jwtUtil.genRefreshToken(username);
                // 将新 Token 放入响应头返回给前端
                response.setHeader("Authorization", "Bearer " + newToken);
//                response.setHeader("Refresh", newRefreshToken);
            }
            // 构建认证对象并设置到安全上下文中
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            sendError(response, "Token expired or invalid");
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
