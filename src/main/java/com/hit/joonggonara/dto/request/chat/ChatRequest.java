package com.hit.joonggonara.dto.request.chat;

import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.ChatRoom;
import com.hit.joonggonara.entity.Member;

public record ChatRequest(
        String message,
        String image,
        String createdMessageDate
) {
    public static ChatRequest of(
            String message,
            String image,
            String createdMessageDate
    ){
        return new ChatRequest( message,image, createdMessageDate);
    }

    public Chat toEntity(ChatRoom chatRoom) {
        return Chat.builder()
                .message(message)
                .image(image)
                .createdMassageDate(createdMessageDate)
                .chatRoom(chatRoom)
                .build();
    }
}
