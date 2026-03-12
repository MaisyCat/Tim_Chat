package com.qchat.tim_chat.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qchat.tim_chat.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    boolean check_user_for_login(String username, String password);
    boolean check_user_for_signup(String username);
    boolean save_user(User user);
    User get_user(String username);
    List<User> get_users(LambdaQueryWrapper<User> lambdaWrapper);
    Long get_userid_from_name(String username);
    List<User> get_users_from_name(String username);
    String getUsernameById(Long id);
}
