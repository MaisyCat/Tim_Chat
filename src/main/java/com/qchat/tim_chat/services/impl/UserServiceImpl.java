package com.qchat.tim_chat.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qchat.tim_chat.entities.User;
import com.qchat.tim_chat.mappers.UserMapper;
import com.qchat.tim_chat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean check_user_for_login(String username, String password) {
        User user=userMapper.selectByUsername(username);
        if(user==null){
            return false;
        }
        // 使用 BCrypt 验证密码
        if(!passwordEncoder.matches(password, user.getPassword())){
            return false;
        }
        return true;
    }
    @Override
    public boolean check_user_for_signup(String username) {
        User user=userMapper.selectByUsername(username);
        if(user==null){
            return false;
        }
        return true;
    }

    @Override
    public boolean save_user(User user) {
        // 使用 BCrypt 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.insert(user) != 0;
    }

    @Override
    public User get_user(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public List<User> get_users(LambdaQueryWrapper<User> lambdaWrapper) {
        return userMapper.selectList(lambdaWrapper);
    }

    @Override
    public Long get_userid_from_name(String username) {
        User user=userMapper.selectByUsername(username);
        if(user==null){
            return null;

        }
        return user.getUserid();
    }

    @Override
    public List<User> get_users_from_name(String username) {
        return userMapper.selectByMoHuName("%"+username+"%");
    }

    @Override
    public String getUsernameById(Long id) {
        return userMapper.selectById(id).getUsername();
    }


}
