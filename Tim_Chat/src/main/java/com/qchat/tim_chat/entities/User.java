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
 * 用户实体类
 * 对应数据库表 t_users
 * 存储用户基本信息和账号凭证
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_users")
public class User {
    /**
     * 用户 ID（主键，自增）
     */
    @TableId(value = "userid",type = IdType.AUTO)
    private Long userid;
    
    /**
     * 用户名（唯一）
     */
    private String username;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 性别
     */
    private String sex;
    
    /**
     * 个性签名
     */
    private String soi;
    
    /**
     * 账号创建时间
     */
    private Timestamp create_time;
    
    /**
     * 账号状态
     */
    private String status;


}

