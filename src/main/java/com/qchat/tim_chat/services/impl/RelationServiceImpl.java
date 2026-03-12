package com.qchat.tim_chat.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qchat.tim_chat.entities.FriendRelation;
import com.qchat.tim_chat.entities.Requests;
import com.qchat.tim_chat.entities.User;
import com.qchat.tim_chat.mappers.FriendMapper;
import com.qchat.tim_chat.mappers.RequestMapper;
import com.qchat.tim_chat.mappers.UserMapper;
import com.qchat.tim_chat.services.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Service
public class RelationServiceImpl implements RelationService {
    private final FriendMapper friendMapper;
    private final RequestMapper requestMapper;
    private final UserMapper userMapper;

    @Autowired
    public RelationServiceImpl(FriendMapper friendMapper, RequestMapper requestMapper, UserMapper userMapper) {
        this.friendMapper = friendMapper;
        this.requestMapper = requestMapper;
        this.userMapper = userMapper;
    }
    @Override
    public boolean okToFriend(FriendRelation friendRelation) {
        int flag=friendMapper.insert(friendRelation);
        Long newFriend_id=friendRelation.getMy_uid();
        friendRelation.setMy_uid(friendRelation.getFriend_uid());
        friendRelation.setFriend_uid(newFriend_id);
        int flag1=friendMapper.insert(friendRelation);
        return flag1 != 0 && flag != 0 && requestMapper.deleteById(friendRelation.getMy_uid(),friendRelation.getFriend_uid());
    }

    public List<User> getFriends(Long uid) {

        List<Long> userIds=friendMapper.getFriendIdsByUid(uid);
        // 在调用selectByIds方法前检查
        if (userIds != null && !userIds.isEmpty()) {
            return userMapper.selectByIds(userIds);
            // 处理结果
        } else {
            System.err.println("查询用户列表不能为空");
            return Collections.emptyList();
        }

    }

    @Override
    public boolean saveRequest(Requests request) {
        return requestMapper.insert(request) != 0;
    }

    @Override
    public boolean removeRequest(Long sender_uid, Long my_uid) {
        return requestMapper.deleteById(sender_uid,my_uid);
    }
}
