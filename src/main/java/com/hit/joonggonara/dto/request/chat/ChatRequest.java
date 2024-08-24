package com.hit.joonggonara.dto.request.chat;

import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.ChatRoom;

import java.time.LocalDateTime;

public record ChatRequest(
        String message,
        String createMessageDate,
        String senderNickName,
        String chatRoomStatus,
        boolean isFirstMessage

) {
    public static ChatRequest of(
            String message,
            String senderNickName,
            String chatRoomStatus,
            boolean isFirstMessage
    ){
        return new ChatRequest(message,LocalDateTime.now().toString(), senderNickName, chatRoomStatus, isFirstMessage);
    }

    public Chat toEntity(ChatRoom chatRoom) {
        return Chat.builder()
                .message(message)
                .createdMassageDate(LocalDateTime.now().toString())
                .senderNickName(senderNickName)
                .isFirstMessage(isFirstMessage)
                .chatRoom(chatRoom)
                .build();
    }
}
