package com.hit.joonggonara.dto.response.chat;

import com.hit.joonggonara.entity.Chat;
import com.hit.joonggonara.entity.ChatRoom;

public record ChatRoomResponse(
        Long roomId,
        String message,
        String lastChatTime,
        String senderNickName,
        String recipientNickName
) {
    public static ChatRoomResponse of(
            Long roomId,
            String message,
            String lastChatTime,
            String senderNickName,
            String recipientNickName
    ){
        return new ChatRoomResponse(roomId,message, lastChatTime,senderNickName, recipientNickName);
    }

    public static ChatRoomResponse empty(){
        return new ChatRoomResponse(null, null,null, null, null);
    }
    public static ChatRoomResponse fromResponse(Chat chat){
        if(chat == null){
            return ChatRoomResponse.empty();
        }
        return ChatRoomResponse.of(
                chat.getChatRoom().getId(),
                chat.getMessage(),
                chat.getCreatedMassageDate(),
                null,
                null
        );
    }
}
