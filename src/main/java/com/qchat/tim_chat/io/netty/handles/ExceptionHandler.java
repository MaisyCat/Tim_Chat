package com.qchat.tim_chat.io.netty.handles;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Netty 异常处理器
 * 继承 ChannelInboundHandlerAdapter，处理通道异常事件
 * 
 * 主要功能：
 * 1. 捕获并记录通道中的异常
 * 2. 打印异常堆栈信息
 * 3. 关闭异常通道，防止资源泄漏
 */
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
    /**
     * 处理通道异常
     * @param ctx 上下文对象
     * @param cause 异常对象
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Exception in channel: " + ctx.channel());
        cause.printStackTrace(); // 打印完整堆栈
        ctx.close(); // 关闭连接
    }
}