package com.hit.joonggonara.dto.response.chat;

import com.hit.joonggonara.entity.Chat;

import java.util.List;
import java.util.stream.Collectors;

public record ChatResponse(
        Long chatId,
        String message,
        String createdMessageDate,
        String senderNickName,
        boolean isFirstMessage
) {

    public static ChatResponse of(
            Long chatId,
            String message,
            String createdMessageDate,
            String senderNickName,
            boolean isFirstMessage

    ){
        return new ChatResponse(chatId, message, createdMessageDate, senderNickName, isFirstMessage);
    }

    public static List<ChatResponse> fromResponse(List<Chat> chats){

        return chats.stream().map(chat -> ChatResponse.of(
                chat.getId(),
                chat.getMessage(),
                chat.getCreatedMassageDate(),
                chat.getSenderNickName(),
                chat.isFirstMessage()
        )).collect(Collectors.toList());
    }

    public static ChatResponse fromResponse(Chat chat){
        return ChatResponse.of(
                chat.getId(),
                chat.getMessage(),
                chat.getCreatedMassageDate(),
                chat.getSenderNickName(),
                chat.isFirstMessage()
        );
    }



}
