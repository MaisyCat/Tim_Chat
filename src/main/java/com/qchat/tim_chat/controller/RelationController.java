package com.qchat.tim_chat.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qchat.tim_chat.Jwt.JwtUtil;
import com.qchat.tim_chat.entities.FriendRelation;
import com.qchat.tim_chat.entities.Requests;
import com.qchat.tim_chat.entities.User;
import com.qchat.tim_chat.services.RelationService;
import com.qchat.tim_chat.services.RequestsService;
import com.qchat.tim_chat.services.UserService;
import io.jsonwebtoken.lang.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * 好友关系控制器
 * 处理好友关系相关的 HTTP 请求
 * 
 * 主要功能：
 * 1. 接受/拒绝好友请求
 * 2. 发送好友申请
 * 3. 获取好友请求列表
 */
@Slf4j
@Controller
public class RelationController {
    private final JwtUtil jwtUtil;
    private final RelationService relationService;
    private final UserService userService;
    private final RequestsService requestsService;
    @Autowired
    public RelationController(JwtUtil jwtUtil, RelationService relationService, UserService userService, RequestsService requestsService) {
        this.jwtUtil = jwtUtil;
        this.relationService = relationService;
        this.userService = userService;
        this.requestsService = requestsService;
    }
    @PostMapping("newFriends/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestHeader("Authorization") String authToken, @RequestBody Long sender_Uid ){
        // 解析 JWT Token 获取当前用户
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7).trim();
        }
        String username=jwtUtil.getUsernameFromToken(authToken);
        Long my_Uid=userService.get_userid_from_name(username);
        Timestamp created_time=new Timestamp(System.currentTimeMillis());
        FriendRelation friendRelation=new FriendRelation(my_Uid,sender_Uid,created_time);
        if (relationService.okToFriend(friendRelation))
            return ResponseEntity.ok("Friend accepted");
        return ResponseEntity.ok().body("failed");
    }
    @PostMapping("newFriends/refuse")
    public ResponseEntity<?> refuseFriendRequest(@RequestHeader("Authorization") String authToken, @RequestBody Long sender_Uid ){
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7).trim();
        }
        String username=jwtUtil.getUsernameFromToken(authToken);
        Long my_Uid=userService.get_userid_from_name(username);
        if (relationService.removeRequest(sender_Uid,my_Uid)){
            return ResponseEntity.ok("Friend refused");
        }
        return ResponseEntity.ok().body("出错");
    }
    @PostMapping("newFriends/add")
    public ResponseEntity<?> send_request(@RequestHeader("Authorization") String authToken, @RequestBody Long receiver_Uid){
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7).trim();
        }
        String username=jwtUtil.getUsernameFromToken(authToken);
        Long my_Uid=userService.get_userid_from_name(username);
        Timestamp created_time=new Timestamp(System.currentTimeMillis());
        Requests requests=new Requests(my_Uid,receiver_Uid,created_time);
        if(relationService.saveRequest(requests))
            return ResponseEntity.ok().body("success");
        return ResponseEntity.status(501).body("failed");
    }
    @PostMapping("getRequestsList")
    public ResponseEntity<?> getRequestsList(@RequestHeader("Authorization") String authToken ){
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7).trim();
        }
        String username=jwtUtil.getUsernameFromToken(authToken);
        Long my_uid=userService.get_userid_from_name(username);

        List<Long> uids = requestsService.get_sender_uid(my_uid);
        for (Long uid : uids) {
            System.out.println(uid);
        }
        LambdaQueryWrapper<User> lambdaWrapper = new LambdaQueryWrapper<>();
        lambdaWrapper.in(User::getUserid,uids ); // 方法引用 + 值列表
        List<User> userList = userService.get_users(lambdaWrapper);
        for (User u : userList) {
            u.setPassword("######");
        }
        return ResponseEntity.ok().body(userList);
    }


}

