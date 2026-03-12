package com.qchat.tim_chat.io.netty;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户 ID 与 Netty Channel 关联关系管理类
 * 用于维护在线用户与其 WebSocket 连接的映射关系
 * 
 * 主要功能：
 * 1. 存储用户 ID 到 Channel 的映射
 * 2. 根据用户 ID 获取对应的 Channel
 * 3. 打印所有在线用户连接信息（调试用）
 */
public class UserChanelRel {

    /**
     * 用户 ID 与 Channel 的映射表
     * Key: 用户 ID, Value: Netty Channel 对象
     */
    private static HashMap<Long, Channel> manage = new HashMap<>();

    /**
     * 建立用户 ID 与 Channel 的关联
     * @param senderId 用户 ID
     * @param channel Netty Channel 对象
     */
    public static void put(Long senderId, Channel channel) {
        manage.put(senderId, channel);
    }

    /**
     * 根据用户 ID 获取对应的 Channel
     * @param senderId 用户 ID
     * @return Netty Channel 对象
     */
    public static Channel get(Long senderId) {
        return manage.get(senderId);
    }

    /**
     * 输出所有在线用户的连接信息（用于调试）
     */
    public static void output() {
        for (Map.Entry<Long, Channel> entry : manage.entrySet()) {
            System.out.println("UserId:" + entry.getKey()
                    + ",ChannelId:" + entry.getValue().id().asLongText()
            );
        }
    }

}