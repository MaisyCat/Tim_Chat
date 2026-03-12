package com.qchat.tim_chat.io.netty.handles;



import com.google.gson.Gson;
import com.qchat.tim_chat.entities.ChatMsg;
import com.qchat.tim_chat.io.netty.DataContent;
import com.qchat.tim_chat.io.netty.Enum.MsgActionEnum;
import com.qchat.tim_chat.io.netty.UserChanelRel;
import com.qchat.tim_chat.services.ChatService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Netty 聊天消息处理器
 * 继承 SimpleChannelInboundHandler，处理 WebSocket 文本消息
 * 
 * 主要功能：
 * 1. 处理客户端连接/断开事件
 * 2. 解析并处理各种类型的 WebSocket 消息（连接、聊天、心跳、同步等）
 * 3. 维护用户与 Channel 的映射关系
 * 4. 实现消息存储和转发
 * 5. 处理增量消息同步请求
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ChatHandle extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final ChatService chatService;
        
    @Autowired
    public ChatHandle(ChatService chatService) {
        this.chatService = chatService;
    }
        
    /**
     * 读取并处理客户端发送的消息
     * @param ctx 上下文对象
     * @param msg 文本 WebSocket 帧
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String content = msg.text();
        System.out.println("Server received message: " + content);
        Gson gson = new Gson();
        DataContent dataContent = gson.fromJson(content, DataContent.class);
        System.out.println("Server received message: " + dataContent);
        Integer action = dataContent.getAction();
        Channel channel = ctx.channel();
    
        // 处理连接初始化请求
        if (action.equals(MsgActionEnum.CONNECT.type)) {
            Long sender = dataContent.getMsg().get(0).getSender();
            UserChanelRel.put(sender, channel);
            UserChanelRel.output();
    
        // 处理聊天消息
        } else if (action.equals(MsgActionEnum.CHAT.type)) {
            List<ChatMsg> msgList = dataContent.getMsg();
            if (msgList != null && !msgList.isEmpty()) {
                ChatMsg chatMsg = msgList.get(0);
                chatService.save(chatMsg); // 保存到数据库
                // ... 其余代码
                DataContent returnData = new DataContent();
                returnData.setMsg(msgList);
                Channel receiverChannel = UserChanelRel.get(chatMsg.getReceiver());
                if (receiverChannel != null) {
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(returnData))); // 转发给接收者
                }
            }
    
        // 处理心跳保持
        } else if (action.equals(MsgActionEnum.KEEPALIVE.type)) {
            System.out.println("Received heartbeat from channel: " + channel);
        // 处理增量同步请求
        } else if (action.equals(MsgActionEnum.SYNC.type)) {
            // 处理增量同步请求
            Long receiver = dataContent.getMsg().get(0).getReceiver();
            Long senderId = dataContent.getMsg().get(0).getSender();
    
            Long lastSyncTime = dataContent.getMsg().get(0).getTime(); // 假设客户端发送最后同步时间
            DataContent syncResponse = new DataContent();
            syncResponse.setAction(MsgActionEnum.SYNC_RESPONSE.type);
            if (receiver == -1) {
                List<ChatMsg> AllMessage = chatService.getMessagesAfter(senderId, lastSyncTime);
                System.err.println(AllMessage);
                // 构建响应
                syncResponse.setMsg(AllMessage); //
    
                // 发送响应
                Channel requesterChannel = UserChanelRel.get(senderId);
                if (requesterChannel != null) {
                    requesterChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(syncResponse)));
                }
            }
            else {
                // 从数据库获取增量消息
                List<ChatMsg> incrementalMessages = chatService.getMessagesAfter(senderId, receiver, lastSyncTime);
                // 构建响应
                syncResponse.setMsg(incrementalMessages); // 假设 DataContent 支持消息列表
    
                // 发送响应
                Channel requesterChannel = UserChanelRel.get(senderId);
                if (requesterChannel != null) {
                    requesterChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(syncResponse)));
                }
            }
    
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
        System.out.println("Client connected, channel ID: " + ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        users.remove(ctx.channel());
        System.out.println("Client removed, channel ID: " + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 记录详细的异常信息
        System.err.println("===== 捕获到异常 =====");
        System.err.println("客户端通道ID: " + ctx.channel().id().asLongText());
        System.err.println("异常类型: " + cause.getClass().getName());
        System.err.println("异常消息: " + cause.getMessage());

        // 打印完整的堆栈跟踪
        cause.printStackTrace();

        // 如果是数据库相关异常，提取更多信息
        if (cause instanceof DataIntegrityViolationException) {
            DataIntegrityViolationException dive = (DataIntegrityViolationException) cause;
            System.err.println("=== 数据库完整性违规详情 ===");
            System.err.println("SQL状态: " + dive.getCause().getMessage());

            // 获取根因
            Throwable rootCause = dive.getRootCause();
            if (rootCause != null) {
                System.err.println("根因异常: " + rootCause.getClass().getName());
                System.err.println("根因消息: " + rootCause.getMessage());
                if (rootCause instanceof SQLException) {
                    System.err.println("SQL: " + ((SQLException) rootCause).getSQLState());
                }
            }
        }

        // 关闭通道并移除
        ctx.channel().close();
        users.remove(ctx.channel());
        System.err.println("已关闭并移除异常通道");
    }
}