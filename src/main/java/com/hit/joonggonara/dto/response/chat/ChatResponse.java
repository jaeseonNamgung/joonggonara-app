package com.hit.joonggonara.dto.response.chat;

import com.hit.joonggonara.entity.Chat;

public record ChatResponse(
        Long roomId,
        String message,
        String image,
        String createdMessageDate,
        String senderNickName,
        String recipientNickName
) {

    public static ChatResponse of(
            Long roomId,
            String message,
            String image,
            String createdMessageDate,
            String senderNickName,
            String recipientNickName
    ){
        return new ChatResponse(roomId, message, image, createdMessageDate, senderNickName, recipientNickName);
    }

    public static ChatResponse fromResponse(Chat chat){
        return new ChatResponse(
                chat.getId(),
                chat.getMessage(),
                chat.getImage(),
                chat.getCreatedMassageDate(),
                null,
              null
        );
    }


}
