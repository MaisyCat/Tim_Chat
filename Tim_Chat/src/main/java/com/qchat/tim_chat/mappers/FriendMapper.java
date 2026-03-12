package com.qchat.tim_chat.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qchat.tim_chat.entities.FriendRelation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FriendMapper extends BaseMapper<FriendRelation>{
    @Override
    @Insert("INSERT INTO qchat.my_friends(my_uid, friend_uid, created_time) VALUES (#{my_uid}, #{friend_uid}, #{created_time})")
    @Options(useGeneratedKeys = true, keyProperty = "id") // 关键配置
    int insert(FriendRelation entity);

    @Select("SELECT qchat.my_friends.friend_uid from qchat.my_friends where my_uid=#{my_uid} ")
    List<Long> getFriendIdsByUid(Long my_uid);

}
