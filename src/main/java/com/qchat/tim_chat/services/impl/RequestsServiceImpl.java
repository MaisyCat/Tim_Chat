package com.qchat.tim_chat.services.impl;

import com.qchat.tim_chat.mappers.RequestMapper;
import com.qchat.tim_chat.services.RequestsService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class RequestsServiceImpl implements RequestsService {

    private final RequestMapper requestMapper;
    @Autowired
    public RequestsServiceImpl(RequestMapper requestMapper) {
        this.requestMapper = requestMapper;
    }
    @Override
    public List<Long> get_sender_uid(Long user_uid) {
        List<Long> uids = new ArrayList<Long>();
        uids=requestMapper.selectByMyUid(user_uid);
        return uids;
    }

}
