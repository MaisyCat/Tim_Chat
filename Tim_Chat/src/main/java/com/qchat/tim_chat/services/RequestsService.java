package com.qchat.tim_chat.services;

import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface RequestsService {
    List<Long> get_sender_uid(Long user_uid);
}
