package com.qchat.tim_chat.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 聊天消息实体类
 * 对应数据库表 chat_msg
 * 存储用户之间的聊天消息记录
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("chat_msg")
public class ChatMsg {
    /**
     * 消息 ID（主键，自增）
     */
    @TableId(value = "msgid",type = IdType.AUTO)
    private Long msgid;
    
    /**
     * 发送者 ID
     */
    private Long sender;
    
    /**
     * 接收者 ID
     */
    private Long receiver;
    
    /**
     * 消息内容
     */
    private String msg;
    
    /**
     * 消息类型（0-文本，1-图片，2-其他）
     */
    private Integer type;
    
    /**
     * 发送时间戳
     */
    private Long time;
}
