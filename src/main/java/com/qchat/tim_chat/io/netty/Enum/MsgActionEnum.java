package com.qchat.tim_chat.io.netty.Enum;

import lombok.Getter;

/**
 * 消息动作枚举类
 * 定义 WebSocket 消息的类型
 */
@Getter
public enum MsgActionEnum {

    CONNECT(1, "第一次 (或重连) 初始化连接"),
    CHAT(2, "聊天消息"),
    KEEPALIVE(3, "客户端保持心跳"),
    PULL_FRIEND(4, "拉取好友"),
    SYNC(5, "增量同步请求"),
    SYNC_RESPONSE(6, "增量同步响应");
    
    /**
     * 消息类型编号
     */
    public final Integer type;
    
    /**
     * 消息类型描述
     */
    public final String content;

    MsgActionEnum(Integer type, String content){
        this.type = type;
        this.content = content;
    }

}