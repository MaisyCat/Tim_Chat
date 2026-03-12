package com.qchat.tim_chat.controller;

import com.qchat.tim_chat.DTO.TokenResponse;
import com.qchat.tim_chat.Jwt.JwtFilter;
import com.qchat.tim_chat.Jwt.JwtUtil;
import com.qchat.tim_chat.entities.User;
import com.qchat.tim_chat.services.RelationService;
import com.qchat.tim_chat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户相关的 HTTP 请求
 * 
 * 主要功能：
 * 1. 获取当前登录用户信息
 * 2. 查询其他用户信息
 * 3. 获取好友列表
 */
@RestController
public class UserController {
    private final UserService userService;
    private final RelationService relationService;

    @Autowired
    public UserController(UserService userService, RelationService relationService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.relationService = relationService;
    }
    @PostMapping("/profile")
    public ResponseEntity<?> getMyInfo()  {
        // 从安全上下文获取当前认证用户的用户名
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user=userService.get_user(username);
        user.setPassword(null); // 不返回密码字段
        return ResponseEntity.ok(user);
    }
    @PostMapping("getUser")
    public ResponseEntity<User> getUser(@RequestBody String username) {
        username=username.substring(1, username.length() - 1);
        User user=userService.get_user(username);
        user.setPassword(null);
        return ResponseEntity.ok(user);

    }
    @PostMapping("getUserName")
    public ResponseEntity<Map<String,String>> getUserName(@RequestBody Long userId) {
        Map<String,String> map=new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        map.put("username",username);
        return ResponseEntity.ok(map);

    }
    @PostMapping("getMyFriend")
    public ResponseEntity<List<Map<String,Object>>> getMyFriend(@RequestBody long userId) {
        List<Map<String,Object>> response=new ArrayList<>();
        List<User> users=relationService.getFriends(userId);
        for (User user:users) {
            Map<String,Object> map=new HashMap<>();
            map.put("name",user.getUsername());
            map.put("id",user.getUserid());
            map.put("avatar","");
            map.put("firstLetter","");
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }
}
