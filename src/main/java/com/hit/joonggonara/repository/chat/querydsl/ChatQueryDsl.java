package com.hit.joonggonara.repository.chat.querydsl;

import com.hit.joonggonara.entity.Chat;

import java.util.List;

public interface ChatQueryDsl {
    List<Chat> findChatAllByRoomId(Long roomId);
}
