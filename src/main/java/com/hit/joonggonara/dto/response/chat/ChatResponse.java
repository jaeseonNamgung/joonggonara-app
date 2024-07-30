package com.hit.joonggonara.dto.response.chat;

import com.hit.joonggonara.entity.Chat;

import java.util.List;
import java.util.stream.Collectors;

public record ChatResponse(
        Long chatId,
        String message,
        String createdMessageDate,
        String senderNickName
) {

    public static ChatResponse of(
            Long chatId,
            String message,
            String createdMessageDate,
            String senderNickName

    ){
        return new ChatResponse(chatId, message, createdMessageDate, senderNickName);
    }

    public static List<ChatResponse> fromResponse(List<Chat> chats){

        return chats.stream().map(chat -> ChatResponse.of(
                chat.getId(),
                chat.getMessage(),
                chat.getCreatedMassageDate(),
                chat.getSenderNickName()
        )).collect(Collectors.toList());
    }

    public static ChatResponse fromResponse(Chat chat){
        return ChatResponse.of(
                chat.getId(),
                chat.getMessage(),
                chat.getCreatedMassageDate(),
                chat.getSenderNickName()
        );
    }



}
