package com.qchat.tim_chat.mappers;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qchat.tim_chat.entities.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from qchat.t_users where username=#{username}")
    User selectByUsername(@Param("username") String username);
    @Select("select * from qchat.t_users where username like #{username}")
    List<User> selectByMoHuName(@Param("username") String username );
    @Select("select * from qchat.t_users where userid = #{userid}")
    List<User> selectByUids(@Param("userid") QueryWrapper<User> userid);

}
