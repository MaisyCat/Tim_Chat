package com.qchat.tim_chat.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Primary;

import java.sql.Timestamp;

/**
 * 好友关系实体类
 * 对应数据库表 my_friends
 * 存储用户之间的好友关系
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("my_friends")
public class FriendRelation {
    /**
     * 主键 ID（自增）
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    
    /**
     * 当前用户 ID
     */
    private Long my_uid;
    
    /**
     * 好友用户 ID
     */
    private Long friend_uid;
    
    /**
     * 好友关系创建时间
     */
    private Timestamp created_time;
    
    /**
     * 构造方法（用于创建新的好友关系）
     */
    public FriendRelation(Long my_uid, Long friend_uid, Timestamp created_time) {
        this.my_uid = my_uid;
        this.friend_uid = friend_uid;
        this.created_time = created_time;
    }
}
