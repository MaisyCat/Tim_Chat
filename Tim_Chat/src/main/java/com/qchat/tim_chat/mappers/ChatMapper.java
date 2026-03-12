package com.qchat.tim_chat.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qchat.tim_chat.entities.ChatMsg;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;
import java.util.List;


@Mapper
public interface ChatMapper extends BaseMapper<ChatMsg> {
    @Select("SELECT qchat.chat_msg.msg_id from qchat.chat_msg where sender=#{userid}")
    Long getMsgId(Long userId);
    @Select("SELECT * FROM qchat.chat_msg WHERE (sender=#{sender} && receiver = #{receiver} || sender=#{receiver} && chat_msg.receiver=#{sender}) AND time > #{time}")
    List<ChatMsg> findByReceiverAndTimestampAfter(@Param("sender") Long sender,
            @Param("receiver") Long receiver,
            @Param("time") Long time);
    @Select("SELECT * FROM qchat.chat_msg WHERE (sender=#{sender} || receiver = #{sender}) AND time > #{time}")
    List<ChatMsg> findBySenderAndTimestampAfter(@Param("sender") Long sender,
                                                  @Param("time") Long time);
}
