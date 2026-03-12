package com.qchat.tim_chat.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qchat.tim_chat.entities.Requests;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RequestMapper extends BaseMapper<Requests> {
    @Delete("DELETE FROM requests where receiver_uid=#{my_uid} && requests.sender_uid=#{sender_uid}")
    public boolean deleteById(Long sender_uid,Long my_uid);
    @Select("SELECT sender_uid from requests where receiver_uid=#{my_uid}")
    public List<Long> selectByMyUid(Long my_uid);
}
