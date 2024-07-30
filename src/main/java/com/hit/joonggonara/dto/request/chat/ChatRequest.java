package com.hit.joonggonara.dto.request.chat;

import com.hit.joonggonara.common.type.ChatRoomStatus;
import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.ChatRoom;

import java.time.LocalDateTime;

public record ChatRequest(
        String message,
        String createMessageDate,
        String senderNickName,
        String chatRoomStatus



) {
    public static ChatRequest of(
            String message,
            String senderNickName,
            String chatRoomStatus
    ){
        return new ChatRequest(message,LocalDateTime.now().toString(), senderNickName, chatRoomStatus);
    }

    public Chat toEntity(ChatRoom chatRoom) {
        return Chat.builder()
                .message(message)
                .createdMassageDate(LocalDateTime.now().toString())
                .senderNickName(senderNickName)
                .chatRoom(chatRoom)
                .build();
    }
}
