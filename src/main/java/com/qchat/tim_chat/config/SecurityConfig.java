package com.qchat.tim_chat.config;

import com.qchat.tim_chat.Jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置类
 * 负责配置 HTTP 安全策略、认证授权规则
 * 
 * 主要功能：
 * 1. 禁用 CSRF 保护（适用于 JWT Token 认证）
 * 2. 配置无状态会话管理（STATELESS）
 * 3. 设置 URL 访问权限（认证端点放行，其他需要认证）
 * 4. 注册 JWT 过滤器到过滤器链
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * 配置安全过滤器链
     * @param http HttpSecurity 对象
     * @return SecurityFilterChain 安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /**
         * CSRF 防护配置：禁用 CSRF 保护
         * 由于本项目采用 JWT Token 认证机制，不依赖 Cookie 进行会话管理，
         * 且 API 服务通常为前后端分离架构，因此禁用 CSRF 防护以避免跨域问题
         */
        /**
         * 会话管理策略：配置为无状态（STATELESS）模式
         * 不创建和使用 HTTP Session，每个请求都必须携带 JWT Token 进行认证，
         * 适用于分布式系统和 RESTful API 服务
         */
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 放行认证相关端点（登录/注册/刷新 Token）
                .requestMatchers("/auth/**").permitAll()
                // 放行 Swagger UI 和 API 文档相关接口
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**", "/api-docs/**").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // 添加 JWT 过滤器到过滤器链最前端

        return http.build();
    }

    /**
     * 配置 Web Security 忽略的路径
     * 这些路径将完全跳过 Spring Security 的过滤器链
     * @return WebSecurityCustomizer 配置器
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/api-docs/**"
        );
    }
}
