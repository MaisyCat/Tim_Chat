package com.qchat.tim_chat.services;

import com.qchat.tim_chat.entities.ChatMsg;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public interface ChatService {
    void save(ChatMsg chatMsg);
    List<ChatMsg> getMessagesAfter(Long sender,Long receiver, Long lastSyncTime);
    List<ChatMsg> getMessagesAfter(Long sender,Long lastSyncTime);
}
