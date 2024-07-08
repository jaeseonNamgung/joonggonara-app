package com.hit.joonggonara.dto.request.chat;

import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.ChatRoom;

import java.time.LocalDateTime;

public record ChatRequest(
        String message,
        String createMessageDate,
        String senderNickName
) {
    public static ChatRequest of(
            String message,
            String senderNickName
    ){
        return new ChatRequest(message,LocalDateTime.now().toString(), senderNickName);
    }

    public Chat toEntity(ChatRoom chatRoom) {
        return Chat.builder()
                .message(message)
                .createdMassageDate(createMessageDate)
                .senderNickName(senderNickName)
                .chatRoom(chatRoom)
                .build();
    }
}
