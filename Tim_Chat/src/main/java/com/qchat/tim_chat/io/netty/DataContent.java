package com.qchat.tim_chat.io.netty;

import com.qchat.tim_chat.entities.ChatMsg;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * WebSocket 数据传输对象
 * 封装客户端与服务器之间传递的消息内容
 */
@Data
@Getter
@Setter
public class DataContent implements Serializable {
    /**
     * 消息动作类型（如：CHAT、KEEPALIVE、SYNC 等）
     */
    private Integer action;
    
    /**
     * 聊天消息列表
     */
    private List<ChatMsg> msg;
    
    /**
     * 扩展字段（用于传递额外信息）
     */
    private String expand;

}
