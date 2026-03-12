package com.qchat.tim_chat.services.impl;

import com.qchat.tim_chat.entities.ChatMsg;
import com.qchat.tim_chat.mappers.ChatMapper;
import com.qchat.tim_chat.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatMapper chatMapper;
    @Autowired
    public ChatServiceImpl(ChatMapper chatMapper) {
        this.chatMapper = chatMapper;
    }

    @Override
    public void save(ChatMsg chatMsg) {
        chatMapper.insert(chatMsg);
    }

    @Override
    public List<ChatMsg> getMessagesAfter(Long sender,Long receiver, Long lastSyncTime) {
        return chatMapper.findByReceiverAndTimestampAfter(sender,receiver,lastSyncTime);
    }

    @Override
    public List<ChatMsg> getMessagesAfter(Long sender, Long lastSyncTime) {
        return chatMapper.findBySenderAndTimestampAfter(sender,lastSyncTime);
    }


}
