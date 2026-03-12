package com.qchat.tim_chat.services;

import com.qchat.tim_chat.entities.FriendRelation;
import com.qchat.tim_chat.entities.Requests;
import com.qchat.tim_chat.entities.User;

import java.util.List;

public interface RelationService {
    boolean okToFriend(FriendRelation friendRelation);
    boolean saveRequest(Requests request);
    boolean removeRequest(Long sender_uid,Long my_uid);
    List<User> getFriends(Long My_uid);
}
