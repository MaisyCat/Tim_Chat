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
 * 好友请求实体类
 * 对应数据库表 requests
 * 存储用户发送的好友申请记录
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("requests")
public class Requests {
    /**
     * 主键 ID（自增）
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    
    /**
     * 发送者 ID
     */
    private Long sender_uid;
    
    /**
     * 接收者 ID
     */
    private Long receiver_uid;
    
    /**
     * 请求创建时间
     */
    private Timestamp created_time;
    
    /**
     * 构造方法（用于创建新的好友请求）
     */
    public Requests(Long sender_uid, Long receiver_uid, Timestamp created_time) {
        this.receiver_uid = receiver_uid;
        this.sender_uid = sender_uid;
        this.created_time = created_time;
    }
}
